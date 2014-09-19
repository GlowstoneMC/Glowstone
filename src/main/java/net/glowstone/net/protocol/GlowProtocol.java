package net.glowstone.net.protocol;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.exception.IllegalOpcodeException;
import com.flowpowered.networking.exception.UnknownPacketException;
import com.flowpowered.networking.protocol.AbstractProtocol;
import com.flowpowered.networking.service.CodecLookupService;
import com.flowpowered.networking.service.HandlerLookupService;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.glowstone.GlowServer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class GlowProtocol extends AbstractProtocol {

    private final CodecLookupService inboundCodecs;
    private final CodecLookupService outboundCodecs;
    private final HandlerLookupService handlers;

    public GlowProtocol(String name, int highestOpcode) {
        super(name);
        inboundCodecs = new CodecLookupService(highestOpcode);
        outboundCodecs = new CodecLookupService(highestOpcode);
        handlers = new HandlerLookupService();
    }

    protected <M extends Message, C extends Codec<? super M>, H extends MessageHandler<?, ? super M>> void inbound(int opcode, Class<M> message, Class<C> codec, Class<H> handler) {
        try {
            inboundCodecs.bind(message, codec, opcode);
            handlers.bind(message, handler);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            getLogger().error("Error registering inbound " + opcode + " in " + getName(), e);
        }
    }

    protected <M extends Message, C extends Codec<? super M>> void outbound(int opcode, Class<M> message, Class<C> codec) {
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
            GlowServer.logger.warning("No message handler for: " + clazz.getSimpleName() + " in " + getName());
        }
        return handler;
    }

    @Override
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
            throw new UnknownPacketException("Failed to read packet data (corrupt?)", opcode, length);
        } catch (IllegalOpcodeException e) {
            // go back to before opcode, so that skipping length doesn't skip too much
            buf.resetReaderIndex();
            throw new UnknownPacketException("Opcode received is not a registered codec on the server!", opcode, length);
        }
    }

    @Override
    public <M extends Message> Codec.CodecRegistration getCodecRegistration(Class<M> clazz) {
        Codec.CodecRegistration reg = outboundCodecs.find(clazz);
        if (reg == null) {
            GlowServer.logger.warning("No codec to write: " + clazz.getSimpleName() + " in " + getName());
        }
        return reg;
    }

    @Override
    public ByteBuf writeHeader(ByteBuf out, Codec.CodecRegistration codec, ByteBuf data) {
        final ByteBuf opcodeBuffer = Unpooled.buffer(5);
        ByteBufUtils.writeVarInt(opcodeBuffer, codec.getOpcode());
        ByteBufUtils.writeVarInt(out, opcodeBuffer.readableBytes() + data.readableBytes());
        ByteBufUtils.writeVarInt(out, codec.getOpcode());
        return out;
    }

    public Codec<?> newReadHeader(ByteBuf in) throws Exception {
        int opcode = ByteBufUtils.readVarInt(in);
        return inboundCodecs.find(opcode);
    }
}
