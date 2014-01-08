package net.glowstone.net;

import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.BlockPlacementMessage;
import net.glowstone.net.message.HandshakeMessage;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.Message;
import net.glowstone.net.message.game.PingMessage;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.json.simple.JSONObject;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;

/**
 * A single connection to the server, which may or may not be associated with a
 * player.
 * @author Graham Edgecombe
 */
public final class Session {

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
     * The channel associated with this session.
     */
    private final Channel channel;

    /**
     * The Random for this session
     */
    private final Random random = new Random();

    /**
     * A queue of incoming and unprocessed messages.
     */
    private final Queue<Message> messageQueue = new ArrayDeque<Message>();

    /**
     * The random long used for client-server handshake
     */
    private final String sessionId = Long.toString(random.nextLong(), 16).trim();

    /**
     * The verify token used in authentication
     */
    private byte[] verifyToken;

    /**
     * The verify username used in authentication
     */
    private String verifyUsername;

    /**
     * The current state.
     */
    private ProtocolState state = ProtocolState.HANDSHAKE;

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
     * Creates a new session.
     * @param server The server this session belongs to.
     * @param channel The channel associated with this session.
     */
    public Session(GlowServer server, Channel channel) {
        this.server = server;
        this.channel = channel;
    }

    /**
     * Gets the server associated with this session.
     * @return The server.
     */
    public GlowServer getServer() {
        return server;
    }

    /**
     * Gets the state of this session.
     * @return The session's state.
     */
    public ProtocolState getState() {
        return state;
    }

    /**
     * Sets the state of this session.
     * @param state The new state.
     */
    public void setState(ProtocolState state) {
        this.state = state;
    }

    /**
     * Get the randomly-generated session ID for this session.
     * @return The session id.
     */
    public String getSessionId() {
        return sessionId;
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
     * with this session.
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
     * Returns the address of this session.
     * @return The remote address.
     */
    public InetSocketAddress getAddress() {
        SocketAddress addr = channel.getRemoteAddress();
        if (addr instanceof InetSocketAddress) {
            return (InetSocketAddress) addr;
        } else {
            return null;
        }
    }

    /**
     * Sends a message to the client.
     * @param message The message.
     */
    public void send(Message message) {
        writeTimeoutCounter = 0;
        channel.write(message);
    }

    /**
     * Placeholder method to prevent errors in lots of other places.
     */
    @Deprecated
    public void send(net.glowstone.msg.Message message) {
        GlowServer.logger.info("Send [LEGACY] " + message.getClass().getName());
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
     * @param overrideKick Whether to override the kick event.
     */
    public void disconnect(String reason, boolean overrideKick) {
        if (player != null && !overrideKick) {
            PlayerKickEvent event = EventFactory.onPlayerKick(player, reason);
            if (event.isCancelled()) {
                return;
            }

            reason = event.getReason();

            if (event.getLeaveMessage() != null) {
                server.broadcastMessage(event.getLeaveMessage());
            }
            
            GlowServer.logger.log(Level.INFO, "Player {0} kicked: {1}", new Object[]{player.getName(), reason});
            dispose(false);
        }

        if (state == ProtocolState.HANDSHAKE || state == ProtocolState.STATUS) {
            // No KickMessage in these states
            channel.close();
        } else {
            JSONObject json = new JSONObject();
            json.put("text", reason);
            channel.write(new KickMessage(json)).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public String toString() {
        return Session.class.getName() + " [address=" + channel.getRemoteAddress() + "]";
    }

    /**
     * Pulse this session, performing any updates needed.
     */
    void pulse() {
        readTimeoutCounter++;
        writeTimeoutCounter++;

        Message message;
        while ((message = messageQueue.poll()) != null) {
            if (state == ProtocolState.PLAY && player == null) {
                // player has been unset, we are just seeing extra messages now
                continue;
            }
            if (!MessageMap.getForState(state).callHandler(this, player, message)) {
                GlowServer.logger.warning("Message " + message + " was not handled");
            }
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
     * @param <T> The type of message.
     */
    <T extends Message> void messageReceived(T message) {
        if (message instanceof HandshakeMessage) {
            // must handle immediately, because network reads are affected (a little hacky)
            MessageMap.getForState(state).callHandler(this, player, message);
        } else {
            messageQueue.add(message);
        }
    }

    /**
     * Disposes of this session by destroying the associated player, if there is
     * one.
     */
    void dispose(boolean broadcastQuit) {
        if (player != null) {            
            player.remove();
            /*Message userListMessage = new UserListItemMessage(player.getPlayerListName(), false, (short)0);
            for (Player player : server.getOnlinePlayers()) {
                ((GlowPlayer) player).getSession().send(userListMessage);
            }*/

            String text = EventFactory.onPlayerQuit(player).getQuitMessage();
            if (broadcastQuit && text != null) {
                server.broadcastMessage(text);
            }
            player = null; // in case we are disposed twice
        }
    }

}
