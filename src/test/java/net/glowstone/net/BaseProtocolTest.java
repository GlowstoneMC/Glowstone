package net.glowstone.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.flowpowered.network.Codec;
import com.flowpowered.network.Message;
import com.flowpowered.network.service.CodecLookupService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.glowstone.net.message.play.inv.HeldItemMessage;
import net.glowstone.net.protocol.GlowProtocol;
import net.glowstone.net.protocol.PlayProtocol;
import net.glowstone.testutils.ServerShim;
import org.junit.Test;

/**
 * Base tests for each {@link GlowProtocol}.
 */
public abstract class BaseProtocolTest {

    private final GlowProtocol protocol;
    private final Message[] testMessages;
    private final CodecLookupService inboundCodecs;
    private final CodecLookupService outboundCodecs;

    protected BaseProtocolTest(GlowProtocol protocol, Message[] testMessages) {
        this.protocol = protocol;
        this.testMessages = testMessages;

        // Retrieve codec lookup service from protocol
        try {
            inboundCodecs = getField(protocol, GlowProtocol.class, "inboundCodecs");
            outboundCodecs = getField(protocol, GlowProtocol.class, "outboundCodecs");
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }

        ServerShim.install();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object object, Class<?> clazz, String name)
        throws ReflectiveOperationException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(object);
    }

    @Test
    public void testProtocol() throws Exception {
        Map<Class<? extends Message>, Codec.CodecRegistration> inboundMap = getField(inboundCodecs,
            CodecLookupService.class, "messages");
        Map<Class<? extends Message>, Codec.CodecRegistration> outboundMap = getField(
            outboundCodecs, CodecLookupService.class, "messages");
        Set<Class<? extends Message>> inboundSet = new HashSet<>(inboundMap.keySet());
        Set<Class<? extends Message>> outboundSet = new HashSet<>(outboundMap.keySet());

        for (Message message : testMessages) {
            boolean any = false;
            Class<? extends Message> clazz = message.getClass();

            // test inbound
            Codec.CodecRegistration registration = inboundCodecs.find(clazz);
            if (registration != null) {
                inboundSet.remove(clazz);
                checkCodec(registration, message);
                any = true;
            }

            // test outbound
            registration = outboundCodecs.find(clazz);
            if (registration != null) {
                outboundSet.remove(clazz);
                checkCodec(registration, message);
                any = true;
            }

            assertThat("Codec missing for: " + message, any, is(true));
        }

        // special case: HeldItemMessage is excluded from tests
        inboundSet.remove(HeldItemMessage.class);
        outboundSet.remove(HeldItemMessage.class);

        assertThat("Did not test inbound classes: " + inboundSet, inboundSet.isEmpty(), is(true));
        // todo: enable the outbound check for PlayProtocol
        if (!(protocol instanceof PlayProtocol)) {
            assertThat("Did not test outbound classes: " + outboundSet, outboundSet.isEmpty(),
                is(true));
        }
    }

    private void checkCodec(Codec.CodecRegistration reg, Message message) {
        // check a message with its codec
        try {
            Codec<Message> codec = reg.getCodec();
            ByteBuf buffer = codec.encode(Unpooled.buffer(), message);
            Message decoded = codec.decode(buffer);
            buffer.release();
            assertThat("Asymmetry for " + reg.getOpcode() + "/" + message.getClass().getName(),
                decoded, is(message));
        } catch (IOException e) {
            throw new AssertionError(
                "Error in I/O for " + reg.getOpcode() + "/" + message.getClass().getName(), e);
        }
    }
}
