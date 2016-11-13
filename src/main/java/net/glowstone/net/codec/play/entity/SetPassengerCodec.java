package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.SetPassengerMessage;

import java.io.IOException;

public class SetPassengerCodec implements Codec<SetPassengerMessage> {
    @Override
    public SetPassengerMessage decode(ByteBuf buffer) throws IOException {
        int entityID = ByteBufUtils.readVarInt(buffer);
        //TODO Read a array of varint?
        return null;
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SetPassengerMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getEntityID());
        ByteBufUtils.writeVarInt(buf, message.getPassengers().length);
        for (int passenger : message.getPassengers()) {
            ByteBufUtils.writeVarInt(buf, passenger);
        }
        return buf;
    }
}
