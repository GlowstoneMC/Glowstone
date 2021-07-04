package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.SetCooldownMessage;

import java.io.IOException;

public class SetCooldownCodec implements Codec<SetCooldownMessage> {

    @Override
    public SetCooldownMessage decode(ByteBuf buffer) throws IOException {
        int itemId = ByteBufUtils.readVarInt(buffer);
        int cooldownTicks = ByteBufUtils.readVarInt(buffer);
        return new SetCooldownMessage(itemId, cooldownTicks);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SetCooldownMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getItemId());
        ByteBufUtils.writeVarInt(buf, message.getCooldownTicks());
        return buf;
    }
}
