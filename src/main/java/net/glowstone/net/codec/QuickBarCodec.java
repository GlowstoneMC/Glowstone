package net.glowstone.net.codec;

import net.glowstone.msg.QuickBarMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public class QuickBarCodec extends MessageCodec<QuickBarMessage> {

    public QuickBarCodec() {
        super(QuickBarMessage.class, 0x6B);
    }

    @Override
    public ChannelBuffer encode(QuickBarMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(9);
        buffer.writeShort(message.getSlot());
        buffer.writeShort(message.getId());
        buffer.writeShort(message.getAmount());
        buffer.writeShort(message.getDamage());
        return buffer;
    }

    @Override
    public QuickBarMessage decode(ChannelBuffer buffer) throws IOException {
        short slot = buffer.readShort();
        short id = buffer.readShort();
        short amount = buffer.readShort();
        short damage = buffer.readShort();
        return new QuickBarMessage(slot, id, amount, damage);
    }
    
}
