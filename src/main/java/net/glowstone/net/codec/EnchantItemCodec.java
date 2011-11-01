package net.glowstone.net.codec;

import net.glowstone.msg.EnchantItemMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public class EnchantItemCodec extends MessageCodec<EnchantItemMessage> {
    
    public EnchantItemCodec() {
        super(EnchantItemMessage.class, 0x6C);
    }

    @Override
    public ChannelBuffer encode(EnchantItemMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(3);
        buffer.writeShort(message.getTransaction());
        buffer.writeByte(message.getEnchantment());
        return buffer;
    }

    @Override
    public EnchantItemMessage decode(ChannelBuffer buffer) throws IOException {
        int transaction = buffer.readShort();
        int enchantment = buffer.readByte();
        return new EnchantItemMessage(transaction, enchantment);
    }
}
