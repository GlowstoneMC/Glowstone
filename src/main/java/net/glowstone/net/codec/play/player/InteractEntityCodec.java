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
        if (action == InteractEntityMessage.Action.INTERACT_AT.ordinal()) {
            float targetX = buf.readFloat();
            float targetY = buf.readFloat();
            float targetZ = buf.readFloat();
            int hand = ByteBufUtils.readVarInt(buf);
            return new InteractEntityMessage(id, action, targetX, targetY, targetZ, hand);
        } else if (action == InteractEntityMessage.Action.INTERACT.ordinal()) {
            int hand = ByteBufUtils.readVarInt(buf);
            return new InteractEntityMessage(id, action, hand);
        }
        return new InteractEntityMessage(id, action);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, InteractEntityMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeVarInt(buf, message.getAction());
        if (message.getAction() == InteractEntityMessage.Action.INTERACT_AT.ordinal()) {
            buf.writeFloat(message.getTargetX());
            buf.writeFloat(message.getTargetY());
            buf.writeFloat(message.getTargetZ());
            ByteBufUtils.writeVarInt(buf, message.getHand());
        } else if (message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            ByteBufUtils.writeVarInt(buf, message.getHand());
        }
        return buf;
    }
}
