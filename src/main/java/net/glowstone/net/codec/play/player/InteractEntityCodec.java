package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage.Action;

public final class InteractEntityCodec implements Codec<InteractEntityMessage> {

    @Override
    public InteractEntityMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int action = ByteBufUtils.readVarInt(buf);
        if (action == Action.INTERACT_AT.ordinal()) {
            float targetX = buf.readFloat();
            float targetY = buf.readFloat();
            float targetZ = buf.readFloat();
            int hand = ByteBufUtils.readVarInt(buf);
            return new InteractEntityMessage(id, action, targetX, targetY, targetZ, hand);
        } else if (action == Action.INTERACT.ordinal()) {
            int hand = ByteBufUtils.readVarInt(buf);
            return new InteractEntityMessage(id, action, hand);
        }
        return new InteractEntityMessage(id, action);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, InteractEntityMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeVarInt(buf, message.getAction());
        if (message.getAction() == Action.INTERACT_AT.ordinal()) {
            buf.writeFloat(message.getTargetX());
            buf.writeFloat(message.getTargetY());
            buf.writeFloat(message.getTargetZ());
            ByteBufUtils.writeVarInt(buf, message.getHand());
        } else if (message.getAction() == Action.INTERACT.ordinal()) {
            ByteBufUtils.writeVarInt(buf, message.getHand());
        }
        return buf;
    }
}
