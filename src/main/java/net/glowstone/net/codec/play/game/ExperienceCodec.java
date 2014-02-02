package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.ExperienceMessage;

import java.io.IOException;

public final class ExperienceCodec implements Codec<ExperienceMessage> {
    public ExperienceMessage decode(ByteBuf buffer) throws IOException {
        float barValue = buffer.readFloat();
        int level = buffer.readShort();
        int totalExp = buffer.readShort();
        return new ExperienceMessage(barValue, level, totalExp);
    }

    public ByteBuf encode(ByteBuf buf, ExperienceMessage message) throws IOException {
        buf.writeFloat(message.getBarValue());
        buf.writeShort(message.getLevel());
        buf.writeShort(message.getTotalExp());
        return buf;
    }
}
