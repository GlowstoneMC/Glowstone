package net.glowstone.net.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import net.glowstone.GlowServer;

/**
 * Experimental pipeline component.
 */
public final class EncryptionHandler extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private final CryptBuf encodeBuf;
    private final CryptBuf decodeBuf;

    /**
     * Creates an instance that applies symmetrical AES encryption.
     *
     * @param sharedSecret an AES key
     */
    public EncryptionHandler(SecretKey sharedSecret) {
        try {
            encodeBuf = new CryptBuf(Cipher.ENCRYPT_MODE, sharedSecret);
            decodeBuf = new CryptBuf(Cipher.DECRYPT_MODE, sharedSecret);
        } catch (GeneralSecurityException e) {
            // should never happen
            GlowServer.logger.log(Level.SEVERE, "Failed to initialize encrypted channel", e);
            throw new AssertionError("Failed to initialize encrypted channel", e);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
        throws Exception {
        encodeBuf.crypt(msg, out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
        throws Exception {
        decodeBuf.crypt(msg, out);
    }

    private static class CryptBuf {

        private final Cipher cipher;

        private CryptBuf(int mode, SecretKey sharedSecret) throws GeneralSecurityException {
            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(mode, sharedSecret, new IvParameterSpec(sharedSecret.getEncoded()));
        }

        public void crypt(ByteBuf msg, List<Object> out) {
            ByteBuffer outBuffer = ByteBuffer.allocate(msg.readableBytes());

            try {
                cipher.update(msg.nioBuffer(), outBuffer);
            } catch (ShortBufferException e) {
                throw new AssertionError("Encryption buffer was too short", e);
            }

            outBuffer.flip();
            out.add(Unpooled.wrappedBuffer(outBuffer));
        }
    }

}
