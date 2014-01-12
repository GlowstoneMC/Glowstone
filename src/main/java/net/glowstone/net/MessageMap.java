package net.glowstone.net;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.handler.*;
import net.glowstone.net.message.HandshakeMessage;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.Message;
import net.glowstone.net.message.game.*;
import net.glowstone.net.message.login.LoginStartMessage;
import net.glowstone.net.message.login.LoginSuccessMessage;
import net.glowstone.net.message.player.PlayerLookMessage;
import net.glowstone.net.message.player.PlayerPositionLookMessage;
import net.glowstone.net.message.player.PlayerPositionMessage;
import net.glowstone.net.message.player.PlayerUpdateMessage;
import net.glowstone.net.message.status.StatusPingMessage;
import net.glowstone.net.message.status.StatusRequestMessage;
import net.glowstone.net.message.status.StatusResponseMessage;
import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Mappings between opcodes and message types for various states of the protocol.
 */
public final class MessageMap {

    /**
     * The MessageMap for each protocol state.
     */
    private static final MessageMap[] stateMaps = new MessageMap[ProtocolState.values().length];

    private final ProtocolState state;

    private final Map<Class<? extends Message>, Integer> sendTable = new HashMap<Class<? extends Message>, Integer>();

    private final Map<Integer, Class<? extends Message>> receiveTable = new HashMap<Integer, Class<? extends Message>>();

    private final Map<Class<? extends Message>, MessageHandler> handlerTable = new HashMap<Class<? extends Message>, MessageHandler>();

    /**
     * Initialize a new MessageMap.
     */
    private MessageMap(ProtocolState state) {
        if (stateMaps[state.ordinal()] != null) {
            throw new IllegalStateException("MessageMap for " + state + " is already set");
        }
        stateMaps[state.ordinal()] = this;
        this.state = state;
    }

    /**
     * Bind an outgoing opcode and message class.
     */
    private <T extends Message> void bindSend(int opcode, Class<T> clazz) {
        sendTable.put(clazz, opcode);
    }

    /**
     * Bind an incoming opcode and message class, and optionally a handler.
     */
    private <T extends Message, H extends MessageHandler<? super T>> void bindReceive(int opcode, Class<T> clazz, Class<H> handler) {
        receiveTable.put(opcode, clazz);
        if (handler != null) {
            try {
                handlerTable.put(clazz, handler.newInstance());
            } catch (ReflectiveOperationException e) {
                GlowServer.logger.severe("Failed to instantiate handler class " + handler.getSimpleName() + ": " + e);
            }
        }
    }

    /**
     * Decode a message from a channel buffer containing an opcode and data.
     * @param buf The buffer to read from.
     * @return The read message, or null on failure.
     */
    public Message decode(ChannelBuffer buf) {
        int opcode = ChannelBufferUtils.readVarInt(buf);
        String hexcode = "0x" + (opcode < 0x10 ? "0" : "") + Integer.toHexString(opcode);

        // look up the message class
        Class<? extends Message> clazz = receiveTable.get(opcode);
        if (clazz == null) {
            GlowServer.logger.warning("Skipping unknown " + state + " opcode: " + hexcode);
            return null;
        }

        String errorDesc = clazz.getSimpleName() + " (" + state + "/" + hexcode + ")";

        // construct & decode the message
        Message message;
        try {
            Constructor<? extends Message> constructor = clazz.getConstructor(ChannelBuffer.class);
            message = constructor.newInstance(buf);
        } catch (NoSuchMethodException e) {
            GlowServer.logger.severe("No decode constructor for " + errorDesc);
            return null;
        } catch (InvocationTargetException e) {
            GlowServer.logger.log(Level.SEVERE, "Error while decoding " + errorDesc, e.getCause());
            return null;
        } catch (ReflectiveOperationException e) {
            GlowServer.logger.log(Level.SEVERE, "Failed to construct " + errorDesc, e);
            return null;
        }

        // give a warning if there's unread bytes left over
        if (buf.readableBytes() > 0) {
            GlowServer.logger.warning("Decoding " + errorDesc + ": had " + buf.readableBytes() + " left over");
        }

        return message;
    }

    /**
     * Encode a message to a channel buffer, including length and opcode.
     * @param message The message to encode.
     * @param buf The destination buffer.
     */
    public void encode(Message message, ChannelBuffer buf) {
        // look up opcode for message class
        Class<? extends Message> clazz = message.getClass();
        if (!sendTable.containsKey(clazz)) {
            GlowServer.logger.warning("Cannot encode " + state + " message: " + clazz.getSimpleName());
            return;
        }
        int opcode = sendTable.get(clazz);

        // write message
        ChannelBuffer temp = ChannelBuffers.dynamicBuffer();
        ChannelBufferUtils.writeVarInt(temp, opcode);
        message.encode(temp);
        int size = temp.writerIndex();

        // write size
        ChannelBufferUtils.writeVarInt(buf, size);
        buf.writeBytes(temp);
    }

    /**
     * Call the MessageHandler for the given message class.
     * @param session The session being handled.
     * @param player The player being handled.
     * @param message The message to handle.
     * @return Whether a handler existed for the message.
     */
    public <T extends Message> boolean callHandler(Session session, GlowPlayer player, T message) {
        @SuppressWarnings("unchecked")
        MessageHandler<T> handler = handlerTable.get(message.getClass());
        if (handler == null) return false;
        handler.handle(session, player, message);
        return true;
    }

    /**
     * Get the MessageMap for the given protocol state.
     * @param state the protocol state.
     * @return the codec map for the state.
     */
    public static MessageMap getForState(ProtocolState state) {
        return stateMaps[state.ordinal()];
    }

    static {
        MessageMap map;

        // Handshake
        map = new MessageMap(ProtocolState.HANDSHAKE);
        map.bindReceive(0x00, HandshakeMessage.class, HandshakeHandler.class);

        // Status
        map = new MessageMap(ProtocolState.STATUS);
        map.bindReceive(0x00, StatusRequestMessage.class, StatusRequestHandler.class);
        map.bindReceive(0x01, StatusPingMessage.class, StatusPingHandler.class);
        map.bindSend(0x00, StatusResponseMessage.class);
        map.bindSend(0x01, StatusPingMessage.class);

        // Login
        map = new MessageMap(ProtocolState.LOGIN);
        map.bindReceive(0x00, LoginStartMessage.class, LoginStartHandler.class);
        //map.bindReceive(0x01, EncryptResponseMessage.class, null);
        map.bindSend(0x00, KickMessage.class);
        //map.bindSend(0x01, EncryptRequestMessage.class);
        map.bindSend(0x02, LoginSuccessMessage.class);

        // Play
        map = new MessageMap(ProtocolState.PLAY);
        map.bindReceive(0x00, PingMessage.class, PingHandler.class);
        map.bindReceive(0x01, IncomingChatMessage.class, ChatHandler.class);
        map.bindReceive(0x03, PlayerUpdateMessage.class, PlayerUpdateHandler.class);
        map.bindReceive(0x04, PlayerPositionMessage.class, PlayerUpdateHandler.class);
        map.bindReceive(0x05, PlayerLookMessage.class, PlayerUpdateHandler.class);
        map.bindReceive(0x06, PlayerPositionLookMessage.class, PlayerUpdateHandler.class);
        map.bindSend(0x00, PingMessage.class);
        map.bindSend(0x01, JoinGameMessage.class);
        map.bindSend(0x02, ChatMessage.class);
        map.bindSend(0x03, TimeMessage.class);
        map.bindSend(0x05, SpawnPositionMessage.class);
        map.bindSend(0x08, PositionRotationMessage.class);
        map.bindSend(0x21, ChunkDataMessage.class);
        map.bindSend(0x23, BlockChangeMessage.class);
        map.bindSend(0x26, ChunkBulkMessage.class);
        map.bindSend(0x2B, StateChangeMessage.class);
        map.bindSend(0x40, KickMessage.class);
    }

}
