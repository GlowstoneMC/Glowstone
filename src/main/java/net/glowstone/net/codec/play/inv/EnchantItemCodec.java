package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.inv.EnchantItemPacket;

import java.io.IOException;

public final class EnchantItemCodec implements Codec<EnchantItemPacket> {
    @Override
    public EnchantItemPacket decode(ByteBuf buf) throws IOException {
        int window = buf.readByte();
        int enchantment = buf.readByte();
        return new EnchantItemPacket(window, enchantment);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EnchantItemPacket message) throws IOException {
        buf.writeByte(message.getWindow());
        buf.writeByte(message.getEnchantment());
        return buf;
    }
}
