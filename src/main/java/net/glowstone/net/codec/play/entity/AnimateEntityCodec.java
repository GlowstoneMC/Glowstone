package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.AnimateEntityMessage;

public final class AnimateEntityCodec implements Codec<AnimateEntityMessage> {

    @Override
    public AnimateEntityMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int animation = buf.readUnsignedByte();
        return new AnimateEntityMessage(id, animation);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, AnimateEntityMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getAnimation());
        return buf;
    }
}
