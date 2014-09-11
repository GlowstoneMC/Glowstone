package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.HealthMessage;

import java.io.IOException;

public final class HealthCodec implements Codec<HealthMessage> {
    @Override
    public HealthMessage decode(ByteBuf buffer) throws IOException {
        float health = buffer.readFloat();
        int food = ByteBufUtils.readVarInt(buffer);
        float saturation = buffer.readFloat();
        return new HealthMessage(health, food, saturation);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, HealthMessage message) throws IOException {
        buf.writeFloat(message.getHealth());
        ByteBufUtils.writeVarInt(buf, message.getFood());
        buf.writeFloat(message.getSaturation());
        return buf;
    }
}
