package net.glowstone.net;

//import com.flowpowered.networking.process.CommonChannelProcessor;
//import org.bouncycastle.crypto.BufferedBlockCipher;

//public class EncryptionChannelProcessor extends CommonChannelProcessor {
//    private final BufferedBlockCipher cipher;
//    private final byte[] processed;
//    private int stored = 0;
//    private int position = 0;
//
//    public EncryptionChannelProcessor(BufferedBlockCipher cipher, int capacity) {
//        super(capacity);
//        this.cipher = cipher;
//        processed = new byte[capacity * 2];
//    }
//
//    @Override
//    protected void write(byte[] buf, int length) {
//        if (stored > position) {
//            throw new IllegalStateException("Stored data must be completely read before writing more data");
//        }
//        stored = cipher.processBytes(buf, 0, length, processed, 0);
//        position = 0;
//    }
//
//    @Override
//    protected int read(byte[] buf) {
//        if (position >= stored) {
//            return 0;
//        } else {
//            int toRead = Math.min(buf.length, stored - position);
//            for (int i = 0; i < toRead; i++) {
//                buf[i] = processed[position + i];
//            }
//            position += toRead;
//            return toRead;
//        }
//    }
//}