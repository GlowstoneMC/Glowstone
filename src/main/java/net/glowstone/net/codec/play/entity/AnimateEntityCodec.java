package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.AnimateEntityMessage;

import java.io.IOException;

public final class AnimateEntityCodec implements Codec<AnimateEntityMessage> {
    public AnimateEntityMessage decode(ByteBuf buf) throws IOException {
        int id = buf.readInt();
        int animation = buf.readUnsignedByte();
        return new AnimateEntityMessage(id, animation);
    }

    public ByteBuf encode(ByteBuf buf, AnimateEntityMessage message) throws IOException {
        // nb: different than decode!
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getAnimation());
        return buf;
    }
}
