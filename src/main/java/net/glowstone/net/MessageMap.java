package net.glowstone.net;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.handler.HandshakeHandler;
import net.glowstone.net.handler.MessageHandler;
import net.glowstone.net.handler.StatusPingHandler;
import net.glowstone.net.handler.StatusRequestHandler;
import net.glowstone.net.message.*;
import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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
    private <T extends Message, H extends MessageHandler<T>> void bindReceive(int opcode, Class<T> clazz, Class<H> handler) {
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

        // look up the message class
        Class<? extends Message> clazz = receiveTable.get(opcode);
        if (clazz == null) {
            GlowServer.logger.warning("Skipping unknown opcode: " + opcode);
            return null;
        }

        // construct & decode the message
        Message message;
        try {
            Constructor<? extends Message> constructor = clazz.getConstructor(ChannelBuffer.class);
            message = constructor.newInstance(buf);
        } catch (NoSuchMethodException e) {
            GlowServer.logger.severe("No decode constructor for " + clazz.getSimpleName() + " (" + opcode + ")");
            return null;
        } catch (InvocationTargetException e) {
            GlowServer.logger.severe("Failed to decode " + clazz.getSimpleName() + " (" + opcode + "): " + e.getCause());
            return null;
        } catch (ReflectiveOperationException e) {
            GlowServer.logger.severe("Failed to construct " + clazz.getSimpleName() + " (" + opcode + "): " + e);
            return null;
        }

        // give a warning if there's unread bytes left over
        if (buf.readableBytes() > 0) {
            GlowServer.logger.warning("Decoding " + clazz.getSimpleName() + " (" + opcode + "): had " + buf.readableBytes() + " left over");
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
        if (!sendTable.containsKey(message.getClass())) {
            GlowServer.logger.warning("Unknown class to encode: " + message.getClass().getSimpleName());
            return;
        }
        int opcode = sendTable.get(message.getClass());

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

        // Play
        map = new MessageMap(ProtocolState.PLAY);
    }

}
