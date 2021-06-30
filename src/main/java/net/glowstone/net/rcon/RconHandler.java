package net.glowstone.net.rcon;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.event.server.RemoteServerCommandEvent;

/**
 * Handler for Rcon messages.
 */
public class RconHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final byte FAILURE = -1;
    private static final byte TYPE_RESPONSE = 0;
    private static final byte TYPE_COMMAND = 2;
    private static final byte TYPE_LOGIN = 3;

    // FIXME: This is a password stored in plain text!
    private final String password;
    /**
     * The {@link RconServer} this handler belongs to.
     */
    private final RconServer rconServer;
    /**
     * The {@link RconCommandSender} for this connection.
     */
    private final RconCommandSender commandSender;
    private boolean loggedIn;

    /**
     * Creates a remote console handler.
     *
     * @param rconServer the associated server
     * @param password   the remote operator's password
     */
    public RconHandler(RconServer rconServer, String password) {
        this.rconServer = rconServer;
        this.password = password;
        commandSender = new RconCommandSender(rconServer.getServer());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        buf = buf.order(ByteOrder.LITTLE_ENDIAN);
        if (buf.readableBytes() < 8) {
            return;
        }

        int requestId = buf.readInt();
        int type = buf.readInt();

        byte[] payloadData = new byte[buf.readableBytes() - 2];
        buf.readBytes(payloadData);
        String payload = new String(payloadData, StandardCharsets.UTF_8);

        buf.readBytes(2); // two byte padding

        if (type == TYPE_LOGIN) {
            handleLogin(ctx, payload, requestId);
        } else if (type == TYPE_COMMAND) {
            handleCommand(ctx, payload, requestId);
        } else {
            sendLargeResponse(ctx, requestId, "Unknown request " + Integer.toHexString(type));
        }
    }

    private void handleLogin(ChannelHandlerContext ctx, String payload, int requestId) {
        if (password.equals(payload)) {
            loggedIn = true;
            sendResponse(ctx, requestId, TYPE_COMMAND, "");
            GlowServer.logger.info("Rcon connection from [" + ctx.channel().remoteAddress() + "]");
        } else {
            // FIXME: Throttle online brute-force attacks!
            loggedIn = false;
            sendResponse(ctx, FAILURE, TYPE_COMMAND, "");
        }
    }

    private void handleCommand(ChannelHandlerContext ctx, String payload, int requestId) {
        if (!loggedIn) {
            sendResponse(ctx, FAILURE, TYPE_COMMAND, "");
            return;
        }

        try {
            RemoteServerCommandEvent event = EventFactory.getInstance()
                .callEvent(new RemoteServerCommandEvent(commandSender, payload));
            if (event.isCancelled()) {
                return;
            }
            rconServer.getServer().dispatchCommand(commandSender, event.getCommand());

            String message = commandSender.flush();
            if (!rconServer.getServer().useRconColors()) {
                message = ChatColor.stripColor(message);
            }

            sendLargeResponse(ctx, requestId, message);
        } catch (CommandException e) {
            sendLargeResponse(ctx, requestId,
                String.format("Error executing: %s (%s)", payload, e.getMessage()));
        }
    }

    private void sendResponse(ChannelHandlerContext ctx, int requestId, int type, String payload) {
        ByteBuf buf = ctx.alloc().buffer().order(ByteOrder.LITTLE_ENDIAN);
        buf.writeInt(requestId);
        buf.writeInt(type);
        buf.writeBytes(payload.getBytes(StandardCharsets.UTF_8));
        buf.writeByte(0);
        buf.writeByte(0);
        ctx.write(buf);
    }

    private void sendLargeResponse(ChannelHandlerContext ctx, int requestId, String payload) {
        if (payload.isEmpty()) {
            sendResponse(ctx, requestId, TYPE_RESPONSE, "");
            return;
        }

        int start = 0;
        while (start < payload.length()) {
            int length = payload.length() - start;
            int truncated = length > 2048 ? 2048 : length;

            sendResponse(ctx, requestId, TYPE_RESPONSE, payload.substring(start, truncated));
            start += truncated;
        }
    }
}
