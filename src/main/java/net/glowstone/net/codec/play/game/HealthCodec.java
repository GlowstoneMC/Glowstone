package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.HealthPacket;

import java.io.IOException;

public final class HealthCodec implements Codec<HealthPacket> {
    @Override
    public HealthPacket decode(ByteBuf buffer) throws IOException {
        float health = buffer.readFloat();
        int food = ByteBufUtils.readVarInt(buffer);
        float saturation = buffer.readFloat();
        return new HealthPacket(health, food, saturation);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, HealthPacket message) throws IOException {
        buf.writeFloat(message.getHealth());
        ByteBufUtils.writeVarInt(buf, message.getFood());
        buf.writeFloat(message.getSaturation());
        return buf;
    }
}
