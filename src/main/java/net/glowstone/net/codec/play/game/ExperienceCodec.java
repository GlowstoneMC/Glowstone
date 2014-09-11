package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.ExperienceMessage;

import java.io.IOException;

public final class ExperienceCodec implements Codec<ExperienceMessage> {
    @Override
    public ExperienceMessage decode(ByteBuf buffer) throws IOException {
        float barValue = buffer.readFloat();
        int level = ByteBufUtils.readVarInt(buffer);
        int totalExp = ByteBufUtils.readVarInt(buffer);
        return new ExperienceMessage(barValue, level, totalExp);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ExperienceMessage message) throws IOException {
        buf.writeFloat(message.getBarValue());
        ByteBufUtils.writeVarInt(buf, message.getLevel());
        ByteBufUtils.writeVarInt(buf, message.getTotalExp());
        return buf;
    }
}
