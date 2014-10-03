package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.AttachEntityMessage;

import java.io.IOException;

public final class AttachEntityCodec implements Codec<AttachEntityMessage> {
    @Override
    public AttachEntityMessage decode(ByteBuf buf) throws IOException {
        int id = buf.readInt();
        int vehicle = buf.readInt();
        boolean leash = buf.readBoolean();
        return new AttachEntityMessage(id, vehicle, leash);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, AttachEntityMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeInt(message.getVehicle());
        buf.writeBoolean(message.isLeash());
        return buf;
    }
}
