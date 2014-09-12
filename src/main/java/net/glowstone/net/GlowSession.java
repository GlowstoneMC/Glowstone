package net.glowstone.net;

import com.flowpowered.networking.AsyncableMessage;
import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.session.BasicSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.CodecException;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.PlayerProfile;
import net.glowstone.io.PlayerDataService;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.SetCompressionMessage;
import net.glowstone.net.message.play.game.PingMessage;
import net.glowstone.net.message.play.game.UserListItemMessage;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import net.glowstone.net.pipeline.CodecsHandler;
import net.glowstone.net.pipeline.CompressionHandler;
import net.glowstone.net.pipeline.EncryptionHandler;
import net.glowstone.net.pipeline.NoopHandler;
import net.glowstone.net.protocol.GlowProtocol;
import net.glowstone.net.protocol.LoginProtocol;
import net.glowstone.net.protocol.PlayProtocol;
import net.glowstone.net.protocol.ProtocolType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.util.*;
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
    private final Queue<Message> messageQueue = new ArrayDeque<>();

    /**
     * The remote address of the connection.
     */
    private final InetSocketAddress address;

    /**
     * The verify token used in authentication
     */
    private byte[] verifyToken;

    /**
     * The verify username used in authentication
     */
    private String verifyUsername;

    /**
     * A message describing under what circumstances the connection ended.
     */
    private String quitReason;

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
     * The ID of the last ping message sent, used to ensure the client responded correctly.
     */
    private int pingMessageId;

    /**
     * Stores the last block placement message sent, see BlockPlacementHandler.
     */
    private BlockPlacementMessage previousPlacement;

    /**
     * The number of ticks until previousPlacement must be cleared.
     */
    private int previousPlacementTicks;

    /**
     * Creates a new session.
     * @param server The server this session belongs to.
     * @param channel The channel associated with this session.
     */
    public GlowSession(GlowServer server, Channel channel) {
        super(channel, ProtocolType.HANDSHAKE.getProtocol());
        this.server = server;
        address = super.getAddress();
    }

    /**
     * Gets the server associated with this session.
     * @return The server.
     */
    public GlowServer getServer() {
        return server;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Auxiliary state

    /**
     * Get the randomly-generated verify token for this session.
     * @return The verify token
     */
    public byte[] getVerifyToken() {
        return verifyToken;
    }

    /**
     * Sets the verify token of this session.
     * @param verifyToken The verify token.
     */
    public void setVerifyToken(byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }

    /**
     * Gets the verify username for this session.
     * @return The verify username.
     */
    public String getVerifyUsername() {
        return verifyUsername;
    }

    /**
     * Sets the verify username for this session.
     * @param verifyUsername The verify username.
     */
    public void setVerifyUsername(String verifyUsername) {
        this.verifyUsername = verifyUsername;
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
        previousPlacement = message;
        previousPlacementTicks = 2;
    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player and state management

    /**
     * Gets the player associated with this session.
     * @return The player, or {@code null} if no player is associated with it.
     */
    public GlowPlayer getPlayer() {
        return player;
    }

    /**
     * Sets the player associated with this session.
     * @param profile The player's profile with name and UUID information.
     * @throws IllegalStateException if there is already a player associated
     * with this session.
     */
    public void setPlayer(PlayerProfile profile) {
        if (this.player != null) {
            throw new IllegalStateException("Cannot set player twice");
        }

        // isActive check here in case player disconnected during authentication
        if (!isActive()) {
            // no need to call onDisconnect() since it only does anything if there's a player set
            return;
        }

        // initialize the player
        PlayerDataService.PlayerReader reader = server.getPlayerDataService().beginReadingData(profile.getUniqueId());
        player = new GlowPlayer(this, profile, reader);

        // isActive check here in case player disconnected after authentication,
        // but before the GlowPlayer initialization was completed
        if (!isActive()) {
            // todo: we might be racing with the network thread here,
            // could cause onDisconnect() logic to happen twice
            onDisconnect();
            return;
        }

        // login event
        PlayerLoginEvent event = EventFactory.onPlayerLogin(player);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            disconnect(event.getKickMessage(), true);
            return;
        }
        player.getWorld().getRawPlayers().add(player);

        GlowServer.logger.info(player.getName() + " [" + address + "] connected, UUID: " + player.getUniqueId());

        // message and user list
        String message = EventFactory.onPlayerJoin(player).getJoinMessage();
        if (message != null) {
            server.broadcastMessage(message);
        }

        // todo: display names are included in the outgoing messages here, but
        // don't show up on the client. A workaround or proper fix is needed.
        Message addMessage = new UserListItemMessage(UserListItemMessage.Action.ADD_PLAYER, player.getUserListEntry());
        List<UserListItemMessage.Entry> entries = new ArrayList<>();
        for (GlowPlayer other : server.getOnlinePlayers()) {
            if (other != player) {
                other.getSession().send(addMessage);
            }
            entries.add(other.getUserListEntry());
        }
        send(new UserListItemMessage(UserListItemMessage.Action.ADD_PLAYER, entries));
    }

    @Override
    public ChannelFuture sendWithFuture(Message message) {
        writeTimeoutCounter = 0;
        if (!isActive()) {
            // discard messages sent if we're closed, since this happens a lot
            return null;
        }
        return super.sendWithFuture(message);
    }

    @Override
    @Deprecated
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
            GlowServer.logger.info(player.getName() + " kicked: " + reason);
        } else {
            GlowServer.logger.info("[" + address + "] kicked: " + reason);
        }

        if (quitReason == null) {
            quitReason = "kicked";
        }

        // perform the kick, sending a kick message if possible
        if (isActive() && (getProtocol() instanceof LoginProtocol || getProtocol() instanceof PlayProtocol)) {
            // channel is both currently connected and in a protocol state allowing kicks
            sendWithFuture(new KickMessage(reason)).addListener(ChannelFutureListener.CLOSE);
        } else {
            getChannel().close();
        }
    }

    /**
     * Pulse this session, performing any updates needed.
     */
    void pulse() {
        readTimeoutCounter++;
        writeTimeoutCounter++;

        // drop the previous placement if needed
        if (previousPlacementTicks > 0 && --previousPlacementTicks == 0) {
            previousPlacement = null;
        }

        // process messages
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
        if (readTimeoutCounter >= TIMEOUT_TICKS) {
            if (pingMessageId == 0 && getProtocol() instanceof PlayProtocol) {
                pingMessageId = random.nextInt();
                send(new PingMessage(pingMessageId));
            } else {
                disconnect("Timed out");
            }
            readTimeoutCounter = 0;
        }

        // let the client know we haven't timed out yet
        if (writeTimeoutCounter >= TIMEOUT_TICKS && getProtocol() instanceof PlayProtocol) {
            pingMessageId = random.nextInt();
            send(new PingMessage(pingMessageId));
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Pipeline management

    public void setProtocol(ProtocolType protocol) {
        getChannel().flush();

        GlowProtocol proto = protocol.getProtocol();
        updatePipeline("codecs", new CodecsHandler(proto));
        super.setProtocol(proto);
    }

    public void enableEncryption(SecretKey sharedSecret) {
        updatePipeline("encryption", new EncryptionHandler(sharedSecret));
    }

    public void enableCompression(int threshold) {
        send(new SetCompressionMessage(threshold));
        updatePipeline("compression", new CompressionHandler(threshold));
    }

    public void disableCompression() {
        send(new SetCompressionMessage(-1));
        updatePipeline("compression", NoopHandler.INSTANCE);
    }

    private void updatePipeline(String key, ChannelHandler handler) {
        getChannel().pipeline().replace(key, key, handler);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Handler overrides

    @Override
    public void onDisconnect() {
        if (player != null) {
            player.remove();
            Message userListMessage = UserListItemMessage.removeOne(player.getUniqueId());
            for (GlowPlayer player : server.getOnlinePlayers()) {
                player.getSession().send(userListMessage);
            }

            String message = player.getName() + " [" + address + "] disconnected";
            if (quitReason != null) {
                message += ": " + quitReason;
            }
            GlowServer.logger.info(message);

            String text = EventFactory.onPlayerQuit(player).getQuitMessage();
            if (text != null) {
                server.broadcastMessage(text);
            }
            player = null; // in case we are disposed twice
        }
    }

    @Override
    public void messageReceived(Message message) {
        if (message instanceof AsyncableMessage && ((AsyncableMessage) message).isAsync()) {
            // async messages get their handlers called immediately
            super.messageReceived(message);
        } else {
            messageQueue.add(message);
        }
    }

    @Override
    public void onInboundThrowable(Throwable t) {
        if (t instanceof CodecException) {
            // generated by the pipeline, not a network error
            GlowServer.logger.log(Level.SEVERE, "Error in network input", t);
        } else {
            // probably a network-level error - consider the client gone
            if (quitReason == null) {
                quitReason = "read error: " + t;
            }
            getChannel().close();
        }
    }

    @Override
    public void onOutboundThrowable(Throwable t) {
        if (t instanceof CodecException) {
            // generated by the pipeline, not a network error
            GlowServer.logger.log(Level.SEVERE, "Error in network output", t);
        } else {
            // probably a network-level error - consider the client gone
            if (quitReason == null) {
                quitReason = "write error: " + t;
            }
            getChannel().close();
        }
    }

    @Override
    public void onHandlerThrowable(Message message, MessageHandler<?, ?> handle, Throwable t) {
        // can be safely logged and the connection maintained
        GlowServer.logger.log(Level.SEVERE, "Error while handling " + message + " (handler: " + handle.getClass().getSimpleName() + ")", t);
    }

    @Override
    public String toString() {
        if (player != null) {
            return player.getName() + "[" + address + "]";
        } else {
            return "[" + address + "]";
        }
    }
}
