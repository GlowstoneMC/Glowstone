package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.inv.EnchantItemMessage;

public final class EnchantItemCodec implements Codec<EnchantItemMessage> {

    @Override
    public EnchantItemMessage decode(ByteBuf buf) throws IOException {
        int window = buf.readByte();
        int enchantment = buf.readByte();
        return new EnchantItemMessage(window, enchantment);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EnchantItemMessage message) throws IOException {
        buf.writeByte(message.getWindow());
        buf.writeByte(message.getEnchantment());
        return buf;
    }
}
