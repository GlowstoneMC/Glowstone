package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.CooldownPacket;

import java.io.IOException;

public class SetCooldownCodec implements Codec<CooldownPacket> {
    @Override
    public CooldownPacket decode(ByteBuf buffer) throws IOException {
        int itemID = ByteBufUtils.readVarInt(buffer);
        int cooldownTicks = ByteBufUtils.readVarInt(buffer);
        return new CooldownPacket(itemID, cooldownTicks);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CooldownPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getItemID());
        ByteBufUtils.writeVarInt(buf, message.getCooldownTicks());
        return buf;
    }
}
