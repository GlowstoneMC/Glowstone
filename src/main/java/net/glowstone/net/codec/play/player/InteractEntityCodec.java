package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.InteractEntityMessage;

import java.io.IOException;

public final class InteractEntityCodec implements Codec<InteractEntityMessage> {
    @Override
    public InteractEntityMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int action = ByteBufUtils.readVarInt(buf);
        if (action == InteractEntityMessage.Action.ATTACK_AT.ordinal()) {
            float targetX = buf.readFloat();
            float targetY = buf.readFloat();
            float targetZ = buf.readFloat();
            return new InteractEntityMessage(id, action, targetX, targetY, targetZ);
        }
        return new InteractEntityMessage(id, action);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, InteractEntityMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeVarInt(buf, message.getAction());
        if (message.getAction() == InteractEntityMessage.Action.ATTACK_AT.ordinal()) {
            buf.writeFloat(message.getTargetX());
            buf.writeFloat(message.getTargetY());
            buf.writeFloat(message.getTargetZ());
        }
        return buf;
    }
}
