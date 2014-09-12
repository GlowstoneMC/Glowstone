package net.glowstone.net;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.Message;
import com.flowpowered.networking.exception.IllegalOpcodeException;
import com.flowpowered.networking.exception.UnknownPacketException;
import com.flowpowered.networking.service.CodecLookupService;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.glowstone.net.protocol.GlowProtocol;
import net.glowstone.testutils.ServerShim;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

public abstract class BaseProtocolTest {

    private final Message[] testMessages;
    private final GlowProtocol protocol;
    private final CodecLookupService codecLookupService;

    protected BaseProtocolTest(GlowProtocol protocol, Message[] testMessages) throws NoSuchFieldException, IllegalAccessException {
        this.testMessages = testMessages;
        this.protocol = protocol;

        //We allow access directly to the inbound class
        // Get the private String field
        final Field field = GlowProtocol.class.getDeclaredField("inboundCodecs");
        // Allow modification on the field
        field.setAccessible(true);
        // Get
        codecLookupService = (CodecLookupService) field.get(this.protocol);
        ServerShim.install();
    }

    @Test
    public void testMessageCodecLookup() throws IllegalOpcodeException {

        for (Message message : testMessages) {
            Codec.CodecRegistration registration = codecLookupService.find(message.getClass());
            if (registration != null) {
                //We have a inbound packet
                try {
                    ByteBuf buffer = Unpooled.buffer();
                    ByteBufUtils.writeVarInt(buffer, 1);
                    ByteBufUtils.writeVarInt(buffer, registration.getOpcode());
                    Codec<Message> codec = (Codec<Message>) protocol.readHeader(buffer);
                    buffer = codec.encode(Unpooled.buffer(), message);
                    Message message1 = codec.decode(buffer);
                    assertEquals(message, message1);
                } catch (UnknownPacketException e) {
                    fail("No codec for opcode" + registration.getOpcode() + " in codec lookup!");
                } catch (IOException e) {
                    fail("Error while doing networking for message " + message.getClass().getName() + ". Opcode:" + registration.getOpcode());
                }
            } else {
                registration = protocol.getCodecRegistration(message.getClass());
                assertNotNull("Message" + message + " did not have a codec!", registration);
                try {
                    ByteBuf buffer = registration.getCodec().encode(Unpooled.buffer(), message);
                    Message message1 = registration.getCodec().decode(buffer);
                    assertEquals(message, message1);
                } catch (IOException e) {
                    fail("Error while doing networking for message " + message.getClass().getName() + ". Opcode:" + registration.getOpcode());
                }
            }
        }
    }

    @Test
    public void testTestCompleteness() throws NoSuchFieldException, IllegalAccessException {
        List<Integer> testedOpcodes = new ArrayList<>();
        for (Message message : testMessages) {
            Codec.CodecRegistration registration = codecLookupService.find(message.getClass());
            if (registration != null) {
                testedOpcodes.add(registration.getOpcode());
            }
        }
        Field privateStringField = CodecLookupService.class.
                getDeclaredField("messages");

        privateStringField.setAccessible(true);
        ConcurrentMap<Class<? extends Message>, Codec.CodecRegistration> codecs = (ConcurrentMap<Class<? extends Message>, Codec.CodecRegistration>) privateStringField.get(codecLookupService);

        for (Map.Entry<Class<? extends Message>, Codec.CodecRegistration> entry : codecs.entrySet()) {
            //TODO Mojang need to fix this: PlayerSwingArmCodec, HeldItemCodec
            if (entry.getValue().getOpcode() == 9) {
                continue;
            }
            assertTrue("Opcode " + entry.getValue().getOpcode() + " not tested for class " + entry.getValue().getCodec().getClass().getName(), testedOpcodes.contains(entry.getValue().getOpcode()));
        }
    }
}
