package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.ExperienceMessage;

public final class ExperienceCodec implements Codec<ExperienceMessage> {

    @Override
    public ExperienceMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        float barValue = buffer.readFloat();
        int level = ByteBufUtils.readVarInt(buffer);
        int totalExp = ByteBufUtils.readVarInt(buffer);
        return new ExperienceMessage(barValue, level, totalExp);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, ExperienceMessage message) throws IOException {
        buf.writeFloat(message.getBarValue());
        ByteBufUtils.writeVarInt(buf, message.getLevel());
        ByteBufUtils.writeVarInt(buf, message.getTotalExp());
        return buf;
    }
}
