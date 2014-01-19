package net.glowstone.net.protocol;

import com.flowpowered.networking.util.ByteBufUtils;
import com.flowpowered.networking.Codec;
import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.exception.IllegalOpcodeException;
import com.flowpowered.networking.exception.UnknownPacketException;
import com.flowpowered.networking.protocol.keyed.KeyedProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.glowstone.GlowServer;

import java.io.IOException;

public abstract class GlowProtocol extends KeyedProtocol {
    /**
     * From Client
     */
    protected static final String INBOUND = "INBOUND";
    /**
     * To Client
     */
    protected static final String OUTBOUND = "OUTBOUND";
    /**
     * The server's default port.
     */
    public static final int DEFAULT_PORT = 25565;
    /**
     * The server's protocol version.
     */
    public static final int VERSION = 4;


    private final GlowServer server;

    public GlowProtocol(GlowServer server, String name, int highestOpcode) {
        super(name, DEFAULT_PORT, highestOpcode + 1);
        this.server = server;
    }

    @Override
    public <M extends Message> MessageHandler<?, M> getMessageHandle(Class<M> clazz) {
        return getHandlerLookupService(INBOUND).find(clazz);
    }

    @Override
    public Codec<?> readHeader(ByteBuf buf) throws UnknownPacketException {
        System.out.println("Read header");
        int length = -1;
        int opcode = -1;
        try {
            length = ByteBufUtils.readVarInt(buf);
            opcode = ByteBufUtils.readVarInt(buf);
            System.out.println("opcode" + opcode);
            System.out.println("Codec registration: " + getCodecLookupService(INBOUND).find(opcode).getOpcode());
            System.out.println("Codec name:" + getCodecLookupService(INBOUND).find(opcode).getCodec().getClass().getName());
            return getCodecLookupService(INBOUND).find(opcode).getCodec();
        } catch (IOException e) {
            throw new UnknownPacketException("Failed to read packet data (corrupt?)", opcode, length);
        } catch (IllegalOpcodeException e) {
            throw new UnknownPacketException("Opcode received is not a registered codec on the server!", opcode, length);
        }
    }

    @Override
    public <M extends Message> Codec.CodecRegistration getCodecRegistration(Class<M> clazz) {
        System.out.println("Get codec registration for class: " + clazz.getName());
        return getCodecLookupService(OUTBOUND).find(clazz);
    }

    @Override
    public void writeHeader(ByteBuf out, Codec.CodecRegistration codec, ByteBuf data) {
        System.out.println("Writing Header, opcode: " + codec.getOpcode());
        final int length = data.readableBytes();
        final ByteBuf opcodeBuffer = Unpooled.buffer();
        ByteBufUtils.writeVarInt(opcodeBuffer, codec.getOpcode());
        ByteBufUtils.writeVarInt(out, length + opcodeBuffer.readableBytes());
        ByteBufUtils.writeVarInt(out, codec.getOpcode());
    }


    public GlowServer getServer() {
        return server;
    }
}