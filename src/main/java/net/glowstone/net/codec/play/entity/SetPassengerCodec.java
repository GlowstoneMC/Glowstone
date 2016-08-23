package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.PassengerPacket;

import java.io.IOException;

public class SetPassengerCodec implements Codec<PassengerPacket> {
    @Override
    public PassengerPacket decode(ByteBuf buffer) throws IOException {
        int entityID = ByteBufUtils.readVarInt(buffer);
        //TODO Read a array of varint?
        return null;
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PassengerPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getEntityID());
        for (int passenger : message.getPassengers()) {
            ByteBufUtils.writeVarInt(buf, passenger);
        }
        return buf;
    }
}
