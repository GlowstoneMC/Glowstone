package net.glowstone.net;

import com.flowpowered.network.AsyncableMessage;
import com.flowpowered.network.ConnectionManager;
import com.flowpowered.network.Message;
import com.flowpowered.network.MessageHandler;
import com.flowpowered.network.protocol.AbstractProtocol;
import com.flowpowered.network.session.BasicSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.CodecException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.io.PlayerDataService.PlayerReader;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.SetCompressionMessage;
import net.glowstone.net.message.login.LoginSuccessMessage;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import net.glowstone.net.message.play.game.PingMessage;
import net.glowstone.net.message.play.game.UserListItemMessage;
import net.glowstone.net.message.play.game.UserListItemMessage.Action;
import net.glowstone.net.message.play.game.UserListItemMessage.Entry;
import net.glowstone.net.pipeline.CodecsHandler;
import net.glowstone.net.pipeline.CompressionHandler;
import net.glowstone.net.pipeline.EncryptionHandler;
import net.glowstone.net.protocol.GlowProtocol;
import net.glowstone.net.protocol.LoginProtocol;
import net.glowstone.net.protocol.PlayProtocol;
import net.glowstone.net.protocol.ProtocolProvider;
import net.glowstone.util.UuidUtils;
import org.bukkit.Statistic;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

/**
 * A single connection to the server, which may or may not be associated with a player.
 *
 * @author Graham Edgecombe
 */
public class GlowSession extends BasicSession {

    /**
     * The server this session belongs to.
     *
     * @return The server.
     */
    @Getter
    private final GlowServer server;

    /**
     * The provider of the protocols.
     */
    private final ProtocolProvider protocolProvider;

    /**
     * The connection manager this session belongs to.
     */
    private final ConnectionManager connectionManager;

    /**
     * A queue of incoming and unprocessed messages.
     */
    private final Queue<Message> messageQueue = new ConcurrentLinkedDeque<>();

    /**
     * The remote address of the connection.
     */
    @Getter
    private volatile InetSocketAddress address;

    /**
     * The state of the connection.
     *
     * @return true if this session's state is online
     */
    @Getter
    private volatile boolean online;
    /**
     * The randomly-generated verify token used in authentication for this session.
     *
     * @param verifyToken the verify token
     * @return The verify token
     */
    @Getter
    @Setter
    private volatile byte[] verifyToken;

    /**
     * The verify username used in authentication.
     */
    @Getter
    @Setter
    private volatile String verifyUsername;

    /**
     * The hostname/port the player used to connect to the server.
     */
    @Getter
    @Setter
    private volatile InetSocketAddress virtualHost;

    /**
     * The version used to connect.
     */
    @Getter
    private volatile int version = -1;

    /**
     * Data regarding a user who has connected through a proxy, used to provide online-mode UUID and
     * properties and other data even if the server is running in offline mode.
     *
     * <p>Null for non-proxied sessions.
     *
     * @return The proxy data to use, or null for an unproxied connection.
     */
    @Getter
    private volatile ProxyData proxyData;

    /**
     * The player associated with this session (if there is one).
     *
     * @return The player, or {@code null} if no player is associated with this session.
     */
    @Getter
    private volatile GlowPlayer player;

    /**
     * The ID of the last ping message sent, used to ensure the client responded correctly.
     */
    private volatile long pingMessageId;

    /**
     * The number of ticks until previousPlacement must be cleared.
     */
    private volatile int previousPlacementTicks;

    /**
     * If the connection has been disconnected.
     */
    private volatile boolean disconnected;

    /**
     * If compression packet has been sent.
     */
    private volatile boolean compresssionSent;

    /**
     * Creates a new session.
     *
     * @param server The server this session belongs to.
     * @param channel The channel associated with this session.
     * @param connectionManager The connection manager to manage connections for this
     *         session.
     */
    public GlowSession(GlowServer server, ProtocolProvider protocolProvider, Channel channel, ConnectionManager connectionManager) {
        super(channel, protocolProvider.handshake);
        this.server = server;
        this.protocolProvider = protocolProvider;
        this.connectionManager = connectionManager;
        address = super.getAddress();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Auxiliary state

    /**
     * Set the {@link ProxyData} for this session.
     *
     * @param proxyData The proxy data to use.
     */
    public void setProxyData(ProxyData proxyData) {
        this.proxyData = proxyData;
        address = proxyData.getAddress();
        virtualHost = InetSocketAddress.createUnresolved(
                proxyData.getHostname(), virtualHost.getPort());
    }

    /**
     * Sets the version. Must only be called once.
     *
     * @param version the version
     * @throws IllegalStateException if the version has already been set
     */
    public void setVersion(int version) {
        if (this.version != -1) {
            throw new IllegalStateException("Cannot set version twice");
        }
        this.version = version;
    }

    /**
     * Notify that the session is currently idle.
     */
    public void idle() {
        if (pingMessageId == 0 && getProtocol() instanceof PlayProtocol) {
            pingMessageId = System.currentTimeMillis();
            send(new PingMessage(pingMessageId));
        } else {
            disconnect("Timed out");
        }
    }

    /**
     * Note that the client has responded to a keep-alive.
     *
     * @param pingId The pingId to check for validity.
     */
    public void pong(long pingId) {
        if (pingId == pingMessageId) {
            pingMessageId = 0;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player and state management

    /**
     * Sets the player associated with this session.
     *
     * @param profile The player's profile with name and UUID information.
     * @throws IllegalStateException if there is already a player associated with this
     *         session.
     */
    public void setPlayer(GlowPlayerProfile profile) {
        if (player != null) {
            throw new IllegalStateException("Cannot set player twice");
        }

        // isActive check here in case player disconnected during authentication
        if (!isActive()) {
            // no need to call onDisconnect() since it only does anything if there's a player set
            return;
        }

        // initialize the player
        PlayerReader reader = server.getPlayerDataService().beginReadingData(profile.getId());
        player = new GlowPlayer(this, profile, reader);
        finalizeLogin(profile);

        // isActive check here in case player disconnected after authentication,
        // but before the GlowPlayer initialization was completed
        if (!isActive()) {
            reader.close();
            onDisconnect();
            return;
        }

        // Kick other players with the same UUID
        for (GlowPlayer other : getServer().getRawOnlinePlayers()) {
            if (other != player && other.getUniqueId().equals(player.getUniqueId())) {
                other.getSession().disconnect("You logged in from another location.", true);
                break;
            }
        }

        // login event
        PlayerLoginEvent event = EventFactory.getInstance()
                .onPlayerLogin(player, virtualHost.toString());
        if (event.getResult() != Result.ALLOWED) {
            disconnect(event.getKickMessage(), true);
            return;
        }

        //joins the player
        player.join(this, reader);

        player.getWorld().getRawPlayers().add(player);

        online = true;

        GlowServer.logger.info(player.getName() + " [" + address + "] connected, UUID: "
                + UuidUtils.toString(player.getUniqueId()));

        // message and user list
        String message = EventFactory.getInstance().onPlayerJoin(player).getJoinMessage();
        if (message != null && !message.isEmpty()) {
            server.broadcastMessage(message);
        }

        Message addMessage = new UserListItemMessage(Action.ADD_PLAYER, player.getUserListEntry());
        List<Entry> entries = new ArrayList<>();
        for (GlowPlayer other : server.getRawOnlinePlayers()) {
            if (other != player && other.canSee(player)) {
                other.getSession().send(addMessage);
            }
            if (player.canSee(other)) {
                entries.add(other.getUserListEntry());
            }
        }
        send(new UserListItemMessage(Action.ADD_PLAYER, entries));
        send(server.createAdvancementsMessage(false, Collections.emptyList(), player));
    }

    @Override
    public ChannelFuture sendWithFuture(Message message) {
        if (!isActive()) {
            // discard messages sent if we're closed, since this happens a lot
            return null;
        }
        return super.sendWithFuture(message);
    }

    /**
     * Send the message and release the specified byte buffer after it is sent.
     *
     * @param message The message.
     * @param buf The byte buffer.
     */
    public void sendAndRelease(Message message, ByteBuf buf) {
        sendWithFuture(message).addListener(f -> buf.release());
    }

    /**
     * Send the message and release the specified byte buffers after it is sent.
     *
     * @param message The message.
     * @param bufs The byte buffers.
     */
    public void sendAndRelease(Message message, ByteBuf... bufs) {
        sendWithFuture(message).addListener(f -> {
            for (ByteBuf buf : bufs) {
                buf.release();
            }
        });
    }

    @Override
    @Deprecated
    public void disconnect() {
        disconnect("No reason specified.");
    }

    /**
     * Disconnects the session with the specified reason.
     *
     * <p>This causes a KickMessage to be sent. When it has been delivered, the channel is closed.
     *
     * @param reason The reason for disconnection.
     */
    public void disconnect(String reason) {
        disconnect(reason, false);
    }

    /**
     * Disconnects the session with the specified reason.
     *
     * <p>This causes a KickMessage to be sent. When it has been delivered, the channel is closed.
     *
     * @param reason The reason for disconnection.
     * @param overrideKick Whether to skip the kick event.
     */
    public void disconnect(String reason, boolean overrideKick) {
        if (player != null && !overrideKick) {
            PlayerKickEvent event = EventFactory.getInstance().onPlayerKick(player, reason);
            if (event.isCancelled()) {
                return;
            }

            reason = event.getReason();

            if (player.isOnline() && event.getLeaveMessage() != null) {
                server.broadcastMessage(event.getLeaveMessage());
            }
        }

        // log that the player was kicked
        if (player != null) {
            GlowServer.logger.info(player.getName() + " kicked: " + reason);
        } else {
            GlowServer.logger.info("[" + address + "] kicked: " + reason);
        }

        // perform the kick, sending a kick message if possible
        if (isActive() && (getProtocol() instanceof LoginProtocol
                || getProtocol() instanceof PlayProtocol)) {
            // channel is both currently connected and in a protocol state allowing kicks
            ChannelFuture future = sendWithFuture(new KickMessage(reason));
            if (future != null) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            getChannel().close();
        }
    }

    /**
     * Pulse this session, performing any updates needed.
     */
    void pulse() {

        // process messages
        Message message;
        while ((message = messageQueue.poll()) != null) {
            if (disconnected) {
                // disconnected, we are just seeing extra messages now
                break;
            }

            super.messageReceived(message);
        }

        // check if the client is disconnected
        if (disconnected) {
            connectionManager.sessionInactivated(this);

            if (player == null) {
                return;
            }

            player.remove();

            Message userListMessage = UserListItemMessage.removeOne(player.getUniqueId());
            for (GlowPlayer player : server.getRawOnlinePlayers()) {
                if (player.canSee(this.player)) {
                    player.getSession().send(userListMessage);
                } else {
                    player.stopHidingDisconnectedPlayer(this.player);
                }
            }

            GlowServer.logger.info(player.getName() + " [" + address + "] lost connection");

            if (player.isSleeping()) {
                player.leaveBed(false);
            }

            Collection<BossBar> bars;
            do {
                bars = player.getBossBars();
                for (BossBar bar : bars) {
                    bar.removePlayer(player);
                    player.removeBossBar(bar);
                }
            } while (!bars.isEmpty());

            String text = EventFactory.getInstance().onPlayerQuit(player).getQuitMessage();
            if (online && text != null && !text.isEmpty()) {
                server.broadcastMessage(text);
            }
            // statistics
            player.incrementStatistic(Statistic.LEAVE_GAME);
            for (Player p : server.getOnlinePlayers()) {
                if (p.getUniqueId().equals(player.getUniqueId())) {
                    continue;
                }
                GlowPlayer other = (GlowPlayer) p;
                if (!other.canSee(player)) {
                    continue;
                }
                other.getSession().send(new DestroyEntitiesMessage(Collections
                        .singletonList(player.getEntityId())));
            }
            player = null; // in case we are disposed twice
        }
    }

    private void finalizeLogin(GlowPlayerProfile profile) {
        // enable compression if needed
        int compression = getServer().getCompressionThreshold();
        if (compression > 0) {
            enableCompression(compression);
        }

        // send login response
        send(new LoginSuccessMessage(UuidUtils.toString(profile.getId()), profile.getName()));
        setProtocol(protocolProvider.play);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Pipeline management

    /**
     * Sets the protocol for this session.
     *
     * @param proto the new protocol
     */
    @Override
    public void setProtocol(AbstractProtocol proto) {
        getChannel().flush();

        updatePipeline("codecs", new CodecsHandler((GlowProtocol)proto));
        super.setProtocol(proto);
    }

    /**
     * Enables encryption or changes the session key.
     *
     * @param sharedSecret the new session key
     */
    public void enableEncryption(SecretKey sharedSecret) {
        updatePipeline("encryption", new EncryptionHandler(sharedSecret));
    }

    /**
     * Enables compression if not already enabled.
     *
     * @param threshold the minimum message size in bytes to compress
     */
    public void enableCompression(int threshold) {
        // set compression can only be sent once
        if (!compresssionSent) {
            send(new SetCompressionMessage(threshold));
            updatePipeline("compression", new CompressionHandler(threshold));
            compresssionSent = true;
        }
    }

    private void updatePipeline(String key, ChannelHandler handler) {
        getChannel().pipeline().replace(key, key, handler);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Handler overrides

    @Override
    public void onDisconnect() {
        disconnected = true;
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
            disconnect("read error: " + t.getMessage(), true);
        }
    }

    @Override
    public void onOutboundThrowable(Throwable t) {
        if (t instanceof CodecException) {
            // generated by the pipeline, not a network error
            GlowServer.logger.log(Level.SEVERE, "Error in network output", t);
        } else {
            // probably a network-level error - consider the client gone
            disconnect("write error: " + t.getMessage(), true);
        }
    }

    @Override
    public void onHandlerThrowable(Message message, MessageHandler<?, ?> handle, Throwable t) {
        //TODO disconnect on error
        // can be safely logged and the connection maintained
        GlowServer.logger.log(Level.SEVERE,
                "Error while handling " + message + " (handler: " + handle.getClass()
                        .getSimpleName() + ")", t);
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
