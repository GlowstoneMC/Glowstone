package net.glowstone.net.query;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import net.glowstone.GlowServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * Class for handling UDP packets according to the minecraft server query protocol.
 *
 * @see QueryServer
 * @see <a href="http://wiki.vg/Query">Protocol Specifications</a>
 */
public class QueryHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final byte ACTION_HANDSHAKE = 9;
    private static final byte ACTION_STATS = 0;

    /**
     * The {@link QueryServer} this handler belongs to.
     */
    private QueryServer queryServer;

    /**
     * Whether the plugin list should be included in responses.
     */
    private boolean showPlugins;

    public QueryHandler(QueryServer queryServer, boolean showPlugins) {
        this.queryServer = queryServer;
        this.showPlugins = showPlugins;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        GlowServer.logger.log(Level.SEVERE, "Error in query handling", cause);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf buf = msg.content();
        if (buf.readableBytes() < 7) {
            return;
        }

        int magic = buf.readUnsignedShort();
        byte type = buf.readByte();
        int sessionId = buf.readInt();

        if (magic != 0xFEFD) {
            return;
        }

        if (type == ACTION_HANDSHAKE) {
            handleHandshake(ctx, msg, sessionId);
        } else if (type == ACTION_STATS) {
            if (buf.readableBytes() < 4) {
                return;
            }
            int token = buf.readInt();
            if (queryServer.verifyChallengeToken(msg.sender(), token)) {
                if (buf.readableBytes() == 4) {
                    handleFullStats(ctx, msg, sessionId);
                } else {
                    handleBasicStats(ctx, msg, sessionId);
                }
            }
        }
    }

    private void handleHandshake(ChannelHandlerContext ctx, DatagramPacket packet, int sessionId) {
        int challengeToken = queryServer.generateChallengeToken(packet.sender());
        ByteBuf out = ctx.alloc().buffer();
        out.writeByte(ACTION_HANDSHAKE);
        out.writeInt(sessionId);
        writeString(out, String.valueOf(challengeToken));
        ctx.write(new DatagramPacket(out, packet.sender()));
    }

    private void handleBasicStats(ChannelHandlerContext ctx, DatagramPacket packet, int sessionId) {
        GlowServer server = queryServer.getServer();

        ByteBuf buf = ctx.alloc().buffer();
        buf.writeByte(ACTION_STATS);
        buf.writeInt(sessionId);
        writeString(buf, server.getMotd());
        writeString(buf, "SMP");
        writeString(buf, server.getWorlds().get(0).getName());
        writeString(buf, String.valueOf(server.getOnlinePlayers().size()));
        writeString(buf, String.valueOf(server.getMaxPlayers()));
        buf.order(ByteOrder.LITTLE_ENDIAN).writeShort(server.getPort());
        writeString(buf, getIpString());
        ctx.write(new DatagramPacket(buf, packet.sender()));
    }

    private void handleFullStats(ChannelHandlerContext ctx, DatagramPacket packet, int sessionId) {
        GlowServer server = queryServer.getServer();

        StringBuilder plugins = new StringBuilder("Glowstone ").append(server.getVersion())
            .append(" on Bukkit ").append(server.getBukkitVersion());
        if (showPlugins) {
            char delim = ':';
            for (Plugin plugin : server.getPluginManager().getPlugins()) {
                plugins.append(delim).append(' ').append(plugin.getDescription().getFullName());
                delim = ';';
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("hostname", server.getMotd());
        data.put("gametype", "SMP");
        data.put("game_id", "MINECRAFT");
        data.put("version", GlowServer.GAME_VERSION);
        data.put("plugins", plugins);
        data.put("map", server.getWorlds().get(0).getName());
        data.put("numplayers", server.getOnlinePlayers().size());
        data.put("maxplayers", server.getMaxPlayers());
        data.put("hostport", server.getPort());
        data.put("hostip", getIpString());

        ByteBuf buf = ctx.alloc().buffer();
        buf.writeByte(ACTION_STATS);
        buf.writeInt(sessionId);
        // constant: splitnum\x00\x80\x00
        buf.writeBytes(
            new byte[] {0x73, 0x70, 0x6C, 0x69, 0x74, 0x6E, 0x75, 0x6D, 0x00, (byte) 0x80, 0x00});
        for (Entry<String, Object> e : data.entrySet()) {
            writeString(buf, e.getKey());
            writeString(buf, String.valueOf(e.getValue()));
        }
        buf.writeByte(0);
        // constant: \x01player_\x00\x00
        buf.writeBytes(new byte[] {0x01, 0x70, 0x6C, 0x61, 0x79, 0x65, 0x72, 0x5F, 0x00, 0x00});
        for (Player player : server.getOnlinePlayers()) {
            writeString(buf, player.getName());
        }
        buf.writeByte(0);
        ctx.write(new DatagramPacket(buf, packet.sender()));
    }

    private void writeString(ByteBuf out, String str) {
        out.writeBytes(str.getBytes(StandardCharsets.UTF_8)).writeByte(0);
    }

    private String getIpString() {
        String address = queryServer.getServer().getIp();
        return address.isEmpty() ? "0.0.0.0" : address;
    }
}
