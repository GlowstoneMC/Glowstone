package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.PlayerAbilitiesMessage;

public final class PlayerAbilitiesCodec implements Codec<PlayerAbilitiesMessage> {

    @Override
    public PlayerAbilitiesMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int flags = buf.readUnsignedByte();
        float flySpeed = buf.readFloat();
        float walkSpeed = buf.readFloat();
        return new PlayerAbilitiesMessage(flags, flySpeed, walkSpeed);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, PlayerAbilitiesMessage message) throws IOException {
        buf.writeByte(message.getFlags());
        buf.writeFloat(message.getFlySpeed());
        buf.writeFloat(message.getWalkSpeed());
        return buf;
    }
}
