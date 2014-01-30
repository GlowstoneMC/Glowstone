package net.glowstone.net;

import com.flowpowered.networking.processor.simple.SimpleMessageProcessor;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class EncryptionChannelProcessor extends SimpleMessageProcessor {

    private CryptBuf encodeBuf;
    private CryptBuf decodeBuf;

    public EncryptionChannelProcessor(byte[] sharedSecret, int capacity) {
        super(capacity);

        CipherParameters parameters = new ParametersWithIV(new KeyParameter(sharedSecret), sharedSecret);
        BufferedBlockCipher encode = new BufferedBlockCipher(new CFBBlockCipher(new AESEngine(), 8));
        BufferedBlockCipher decode = new BufferedBlockCipher(new CFBBlockCipher(new AESEngine(), 8));
        encode.init(true, parameters);
        decode.init(false, parameters);

        this.encodeBuf = new CryptBuf(encode, capacity * 2);
        this.decodeBuf = new CryptBuf(decode, capacity * 2);
    }

    @Override
    protected void writeEncode(byte[] buf, int length) {
        encodeBuf.write(buf, length);
    }

    @Override
    protected int readEncode(byte[] buf) {
        return encodeBuf.read(buf);
    }

    @Override
    protected void writeDecode(byte[] buf, int length) {
        decodeBuf.write(buf, length);
    }

    @Override
    protected int readDecode(byte[] buf) {
        return decodeBuf.read(buf);
    }

    private static class CryptBuf {
        private final BufferedBlockCipher cipher;
        private final byte[] buffer;
        private int writePosition;
        private int readPosition;

        private CryptBuf(BufferedBlockCipher cipher, int bufSize) {
            this.cipher = cipher;
            this.buffer = new byte[bufSize];
        }

        private int read(byte[] dest) {
            if (readPosition >= writePosition) {
                return 0;
            } else {
                int amount = Math.min(dest.length, writePosition - readPosition);
                System.arraycopy(buffer, readPosition, dest, 0, amount);
                readPosition += amount;
                return amount;
            }
        }

        private void write(byte[] src, int length) {
            if (readPosition < writePosition) {
                throw new IllegalStateException("Stored data must be completely read before writing more data");
            }
            writePosition = cipher.processBytes(src, 0, length, buffer, 0);
            readPosition = 0;
        }
    }
}
