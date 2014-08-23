package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.InteractEntityMessage;

import java.io.IOException;

public final class InteractEntityCodec implements Codec<InteractEntityMessage> {
    public InteractEntityMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int action = ByteBufUtils.readVarInt(buf);
        float targetX = buf.readFloat();
        float targetY = buf.readFloat();
        float targetZ = buf.readFloat();
        return new InteractEntityMessage(id, action, targetX, targetY, targetZ);
    }

    public ByteBuf encode(ByteBuf buf, InteractEntityMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeVarInt(buf, message.getAction());
        buf.writeFloat(message.getTargetX());
        buf.writeFloat(message.getTargetY());
        buf.writeFloat(message.getTargetZ());
        return buf;
    }
}
