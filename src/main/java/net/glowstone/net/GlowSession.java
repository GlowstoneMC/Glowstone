package net.glowstone.net;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.exception.UnknownPacketException;
import com.flowpowered.networking.processor.MessageProcessor;
import com.flowpowered.networking.session.BasicSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.DecoderException;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.BlockPlacementMessage;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.play.game.PingMessage;
import net.glowstone.net.protocol.GlowProtocol;
import net.glowstone.net.protocol.HandshakeProtocol;
import net.glowstone.net.protocol.PlayProtocol;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;

/**
 * A single connection to the server, which may or may not be associated with a
 * player.
 * @author Graham Edgecombe
 */
public final class GlowSession extends BasicSession {

    /**
     * The number of ticks which are elapsed before a client is disconnected due
     * to a timeout.
     */
    private static final int TIMEOUT_TICKS = 300;

    /**
     * The server this session belongs to.
     */
    private final GlowServer server;

    /**
     * The Random for this session
     */
    private final Random random = new Random();

    /**
     * A queue of incoming and unprocessed messages.
     */
    private final Queue<Message> messageQueue = new ArrayDeque<Message>();

    /**
     * The verify token used in authentication
     */
    private byte[] verifyToken;

    /**
     * The verify username used in authentication
     */
    private String verifyUsername;

    /**
     * A timeout counter. This is increment once every tick and if it goes above
     * a certain value the session is disconnected.
     */
    private int readTimeoutCounter = 0;

    /**
     * Similar to readTimeoutCounter but for writes.
     */
    private int writeTimeoutCounter = 0;

    /**
     * The player associated with this session (if there is one).
     */
    private GlowPlayer player;

    /**
     * Handling ping messages
     */
    private int pingMessageId;

    /**
     * Stores the last block placement message to work around a bug in the
     * vanilla client where duplicate packets are sent.
     */
    private BlockPlacementMessage previousPlacement;

    /**
     * The MessageProcessor used for encryption, if it has been enabled.
     */
    private MessageProcessor processor;

    /**
     * Creates a new session.
     * @param server  The server this session belongs to.
     * @param channel The channel associated with this session.
     */
    public GlowSession(GlowServer server, Channel channel) {
        super(channel, new HandshakeProtocol(server));
        this.server = server;
    }

    /**
     * Gets the server associated with this session.
     * @return The server.
     */
    public GlowServer getServer() {
        return server;
    }

    /**
     * Sets the verify token of this session.
     * @param verifyToken The verify token.
     */
    public void setVerifyToken(byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }

    /**
     * Get the randomly-generated verify token for this session.
     * @return The verify token
     */
    public byte[] getVerifyToken() {
        return verifyToken;
    }

    /**
     * Sets the verify username for this session.
     * @param verifyUsername The verify username.
     */
    public void setVerifyUsername(String verifyUsername) {
        this.verifyUsername = verifyUsername;
    }

    /**
     * Gets the verify username for this session.
     * @return The verify username.
     */
    public String getVerifyUsername() {
        return verifyUsername;
    }

    /**
     * Note that the client has responded to a keep-alive.
     * @param pingId The pingId to check for validity.
     */
    public void pong(long pingId) {
        if (pingId == pingMessageId) {
            readTimeoutCounter = 0;
            pingMessageId = 0;
        }
    }

    /**
     * Get the saved previous BlockPlacementMessage for this session.
     * @return The message.
     */
    public BlockPlacementMessage getPreviousPlacement() {
        return previousPlacement;
    }

    /**
     * Set the previous BlockPlacementMessage for this session.
     * @param message The message.
     */
    public void setPreviousPlacement(BlockPlacementMessage message) {
        this.previousPlacement = message;
    }

    /**
     * Gets the player associated with this session.
     * @return The player, or {@code null} if no player is associated with it.
     */
    public GlowPlayer getPlayer() {
        return player;
    }

    /**
     * Sets the player associated with this session.
     * @param player The new player.
     * @throws IllegalStateException if there is already a player associated
     *                               with this session.
     */
    public void setPlayer(GlowPlayer player) {
        if (this.player != null)
            throw new IllegalStateException();

        // login event
        this.player = player;
        PlayerLoginEvent event = EventFactory.onPlayerLogin(player);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            disconnect(event.getKickMessage(), true);
            return;
        }
        player.getWorld().getRawPlayers().add(player);

        GlowServer.logger.info(player.getName() + " [" + getAddress() + "] connected, UUID: " + player.getUniqueId());

        // message and user list
        String message = EventFactory.onPlayerJoin(player).getJoinMessage();
        if (message != null) {
            server.broadcastMessage(message);
        }
        /*Message userListMessage = new UserListItemMessage(player.getPlayerListName(), true, (short)readTimeoutCounter);
        for (Player sendPlayer : server.getOnlinePlayers()) {
            ((GlowPlayer) sendPlayer).getSession().send(userListMessage);
            send(new UserListItemMessage(sendPlayer.getPlayerListName(), true, (short)((GlowPlayer)sendPlayer).getSession().readTimeoutCounter));
        }*/
    }

    /**
     * Sends a message to the client.
     * @param message The message.
     */
    @Override
    public ChannelFuture sendWithFuture(Message message) {
        writeTimeoutCounter = 0;
        if (!getChannel().isActive()) {
            // discard messages sent if we're closed, since this happens a lot
            return null;
        }
        return super.sendWithFuture(message);
    }

    /**
     * Placeholder method to prevent errors in lots of other places.
     */
    @Deprecated
    public void send(net.glowstone.msg.Message message) {
        GlowServer.logger.info("Send [LEGACY] " + message.getClass().getSimpleName());
    }

    @Override
    public void disconnect() {
        disconnect("No reason specified.");
    }

    /**
     * Disconnects the session with the specified reason. This causes a
     * KickMessage to be sent. When it has been delivered, the channel
     * is closed.
     * @param reason The reason for disconnection.
     */
    public void disconnect(String reason) {
        disconnect(reason, false);
    }

    /**
     * Disconnects the session with the specified reason. This causes a
     * KickMessage to be sent. When it has been delivered, the channel
     * is closed.
     * @param reason The reason for disconnection.
     * @param overrideKick Whether to skip the kick event.
     */
    private void disconnect(String reason, boolean overrideKick) {
        if (player != null && !overrideKick) {
            PlayerKickEvent event = EventFactory.onPlayerKick(player, reason);
            if (event.isCancelled()) {
                return;
            }

            reason = event.getReason();

            if (event.getLeaveMessage() != null) {
                server.broadcastMessage(event.getLeaveMessage());
            }
        }

        // log that the player was kicked
        if (player != null) {
            GlowServer.logger.log(Level.INFO, "Player {0} kicked: {1}", new Object[]{player.getName(), reason});
        } else {
            GlowServer.logger.log(Level.INFO, "[{0}] kicked: {1}", new Object[]{getAddress(), reason});
        }

        // perform the kick, sending a kick message if possible
        if (getProtocol().getCodecRegistration(KickMessage.class) == null) {
            getChannel().close();
        } else {
            sendWithFuture(new KickMessage(reason)).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void onDisconnect() {
        if (player != null) {
            player.remove();
            /*Message userListMessage = new UserListItemMessage(player.getPlayerListName(), false, (short)0);
            for (Player player : server.getOnlinePlayers()) {
                ((GlowPlayer) player).getSession().send(userListMessage);
            }*/

            GlowServer.logger.info(player.getName() + " [" + getAddress() + "] lost connection");

            String text = EventFactory.onPlayerQuit(player).getQuitMessage();
            if (text != null) {
                server.broadcastMessage(text);
            }
            player = null; // in case we are disposed twice
        }
    }

    /**
     * Pulse this session, performing any updates needed.
     */
    void pulse() {
        readTimeoutCounter++;
        writeTimeoutCounter++;

        Message message;
        while ((message = messageQueue.poll()) != null) {
            if (getProtocol() instanceof PlayProtocol && player == null) {
                // player has been unset, we are just seeing extra messages now
                continue;
            }

            super.messageReceived(message);
            readTimeoutCounter = 0;
        }

        // let us know if the client has timed out yet
        if (readTimeoutCounter >= TIMEOUT_TICKS)
            if (pingMessageId == 0) {
                pingMessageId = random.nextInt();
                send(new PingMessage(pingMessageId));
                readTimeoutCounter = 0;
            } else {
                disconnect("Timed out");
            }

        // let the client know we haven't timed out yet
        if (writeTimeoutCounter >= TIMEOUT_TICKS) {
            pingMessageId = random.nextInt();
            send(new PingMessage(pingMessageId));
        }
    }

    /**
     * Adds a message to the unprocessed queue.
     * @param message The message.
     */
    public void messageReceived(Message message) {
        if (message.isAsync()) {
            // async messages get their handlers called immediately
            super.messageReceived(message);
        } else {
            messageQueue.add(message);
        }
    }

    public void setProtocol(GlowProtocol protocol) {
        super.setProtocol(protocol);
    }

    @Override
    public void onInboundThrowable(Throwable t) {
        if (t instanceof DecoderException) {
            Throwable cause = t.getCause();
            if (cause instanceof UnknownPacketException) {
                UnknownPacketException ex = (UnknownPacketException) cause;
                GlowServer.logger.info("Skipped unknown " + getProtocol().getName() + " opcode: " + ex.getOpcode() + ", length: " + ex.getLength());
                return;
            }
        }
        GlowServer.logger.log(Level.SEVERE, "Error in network input", t);
    }

    @Override
    public void onOutboundThrowable(Throwable t) {
        GlowServer.logger.log(Level.SEVERE, "Error in network output", t);
    }

    @Override
    public void onHandlerThrowable(Message message, MessageHandler<?, ?> handle, Throwable t) {
        GlowServer.logger.log(Level.SEVERE, "Error while handling " + message + " (handler: " + handle.getClass().getSimpleName() + ")", t);
    }

    public void setProcessor(MessageProcessor processor) {
        this.processor = processor;
    }

    @Override
    public MessageProcessor getProcessor() {
        return processor;
    }

}
