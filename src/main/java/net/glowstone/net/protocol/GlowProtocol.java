package net.glowstone.net.protocol;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.exception.IllegalOpcodeException;
import com.flowpowered.networking.exception.UnknownPacketException;
import com.flowpowered.networking.protocol.keyed.KeyedProtocol;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.glowstone.GlowServer;

import java.io.IOException;

public abstract class GlowProtocol extends KeyedProtocol {

    /**
     * Keys for codec lookup.
     */
    protected static final String INBOUND = "INBOUND";
    protected static final String OUTBOUND = "OUTBOUND";

    /**
     * Default port for the protocol. Not used anywhere.
     */
    private static final int DEFAULT_PORT = 25565;

    public GlowProtocol(GlowServer server, String name, int highestOpcode) {
        super(name, DEFAULT_PORT, highestOpcode + 1);
    }

    @Override
    public <M extends Message> MessageHandler<?, M> getMessageHandle(Class<M> clazz) {
        MessageHandler<?, M> handler = getHandlerLookupService(INBOUND).find(clazz);
        if (handler == null) {
            GlowServer.logger.warning("No message handler for: " + clazz.getSimpleName());
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
            return getCodecLookupService(INBOUND).find(opcode).getCodec();
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
        return getCodecLookupService(OUTBOUND).find(clazz);
    }

    @Override
    public void writeHeader(ByteBuf out, Codec.CodecRegistration codec, ByteBuf data) {
        final ByteBuf opcodeBuffer = Unpooled.buffer();
        ByteBufUtils.writeVarInt(opcodeBuffer, codec.getOpcode());
        ByteBufUtils.writeVarInt(out, opcodeBuffer.readableBytes() + data.readableBytes());
        ByteBufUtils.writeVarInt(out, codec.getOpcode());
    }

}