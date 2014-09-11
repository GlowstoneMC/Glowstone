package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.AnimateEntityMessage;

import java.io.IOException;

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
