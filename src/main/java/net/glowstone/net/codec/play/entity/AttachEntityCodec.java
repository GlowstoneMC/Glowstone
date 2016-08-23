package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.AttachEntityPacket;

import java.io.IOException;

public final class AttachEntityCodec implements Codec<AttachEntityPacket> {
    @Override
    public AttachEntityPacket decode(ByteBuf buf) throws IOException {
        int id = buf.readInt();
        int vehicle = buf.readInt();
        boolean leash = buf.readBoolean();
        return new AttachEntityPacket(id, vehicle, leash);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, AttachEntityPacket message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeInt(message.getVehicle());
        buf.writeBoolean(message.isLeash());
        return buf;
    }
}
