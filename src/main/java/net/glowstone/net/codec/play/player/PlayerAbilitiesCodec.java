package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerAbilitiesPacket;

import java.io.IOException;

public final class PlayerAbilitiesCodec implements Codec<PlayerAbilitiesPacket> {
    @Override
    public PlayerAbilitiesPacket decode(ByteBuf buf) throws IOException {
        int flags = buf.readUnsignedByte();
        float flySpeed = buf.readFloat();
        float walkSpeed = buf.readFloat();
        return new PlayerAbilitiesPacket(flags, flySpeed, walkSpeed);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerAbilitiesPacket message) throws IOException {
        buf.writeByte(message.getFlags());
        buf.writeFloat(message.getFlySpeed());
        buf.writeFloat(message.getWalkSpeed());
        return buf;
    }
}
