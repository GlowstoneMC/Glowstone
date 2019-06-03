package net.glowstone.net.protocol;

import com.flowpowered.network.Codec;
import com.flowpowered.network.Codec.CodecRegistration;
import com.flowpowered.network.Message;
import com.flowpowered.network.MessageHandler;
import com.flowpowered.network.exception.IllegalOpcodeException;
import com.flowpowered.network.exception.UnknownPacketException;
import com.flowpowered.network.protocol.AbstractProtocol;
import com.flowpowered.network.service.CodecLookupService;
import com.flowpowered.network.service.HandlerLookupService;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import net.glowstone.GlowServer;

public abstract class GlowProtocol extends AbstractProtocol {

    private final CodecLookupService inboundCodecs;
    private final CodecLookupService outboundCodecs;
    private final HandlerLookupService handlers;

    /**
     * Creates an instance.
     *
     * @param name the name of the protocol
     * @param highestOpcode the highest opcode this protocol will use
     */
    public GlowProtocol(String name, int highestOpcode) {
        super(name);
        inboundCodecs = new CodecLookupService(highestOpcode + 1);
        outboundCodecs = new CodecLookupService(highestOpcode + 1);
        handlers = new HandlerLookupService();
    }

    protected <M extends Message, C extends Codec<? super M>,
            H extends MessageHandler<?, ? super M>> void inbound(
        int opcode, Class<M> message, Class<C> codec, Class<H> handler) {
        try {
            inboundCodecs.bind(message, codec, opcode);
            handlers.bind(message, handler);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            getLogger().error("Error registering inbound " + opcode + " in " + getName(), e);
        }
    }

    protected <M extends Message, C extends Codec<? super M>,
        H extends MessageHandler<?, ? super M>> void inbound(
        int opcode, Class<M> message, Class<C> codec, H handler) {
        try {
            inboundCodecs.bind(message, codec, opcode);
            handlers.bind(message, handler);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            getLogger().error("Error registering inbound " + opcode + " in " + getName(), e);
        }
    }

    protected <M extends Message, C extends Codec<? super M>> void outbound(int opcode,
        Class<M> message, Class<C> codec) {
        try {
            outboundCodecs.bind(message, codec, opcode);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            getLogger().error("Error registering outbound " + opcode + " in " + getName(), e);
        }
    }

    @Override
    public <M extends Message> MessageHandler<?, M> getMessageHandle(Class<M> clazz) {
        MessageHandler<?, M> handler = handlers.find(clazz);
        if (handler == null) {
            GlowServer.logger
                .warning("No message handler for: " + clazz.getSimpleName() + " in " + getName());
        }
        return handler;
    }

    @Override
    @Deprecated
    public Codec<?> readHeader(ByteBuf buf) throws UnknownPacketException {
        int length = -1;
        int opcode = -1;
        try {
            length = ByteBufUtils.readVarInt(buf);

            // mark point before opcode
            buf.markReaderIndex();

            opcode = ByteBufUtils.readVarInt(buf);
            return inboundCodecs.find(opcode);
        } catch (IOException e) {
            throw new UnknownPacketException("Failed to read packet data (corrupt?)", opcode,
                length);
        } catch (IllegalOpcodeException e) {
            // go back to before opcode, so that skipping length doesn't skip too much
            buf.resetReaderIndex();
            throw new UnknownPacketException(
                "Opcode received is not a registered codec on the server!", opcode, length);
        }
    }

    @Override
    public <M extends Message> CodecRegistration getCodecRegistration(Class<M> clazz) {
        CodecRegistration reg = outboundCodecs.find(clazz);
        if (reg == null) {
            GlowServer.logger
                .warning("No codec to write: " + clazz.getSimpleName() + " in " + getName());
        }
        return reg;
    }

    @Override
    @Deprecated
    public ByteBuf writeHeader(ByteBuf out, CodecRegistration codec, ByteBuf data) {
        ByteBuf opcodeBuffer = Unpooled.buffer(5);
        ByteBufUtils.writeVarInt(opcodeBuffer, codec.getOpcode());
        ByteBufUtils.writeVarInt(out, opcodeBuffer.readableBytes() + data.readableBytes());
        opcodeBuffer.release();
        ByteBufUtils.writeVarInt(out, codec.getOpcode());
        return out;
    }

    public Codec<?> newReadHeader(ByteBuf in) throws IOException, IllegalOpcodeException {
        int opcode = ByteBufUtils.readVarInt(in);
        return inboundCodecs.find(opcode);
    }
}
