package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.ExperiencePacket;

import java.io.IOException;

public final class ExperienceCodec implements Codec<ExperiencePacket> {
    @Override
    public ExperiencePacket decode(ByteBuf buffer) throws IOException {
        float barValue = buffer.readFloat();
        int level = ByteBufUtils.readVarInt(buffer);
        int totalExp = ByteBufUtils.readVarInt(buffer);
        return new ExperiencePacket(barValue, level, totalExp);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ExperiencePacket message) throws IOException {
        buf.writeFloat(message.getBarValue());
        ByteBufUtils.writeVarInt(buf, message.getLevel());
        ByteBufUtils.writeVarInt(buf, message.getTotalExp());
        return buf;
    }
}
