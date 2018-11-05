package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.SetPassengerMessage;

public class SetPassengerCodec implements Codec<SetPassengerMessage> {

    @Override
    public SetPassengerMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        int entityId = ByteBufUtils.readVarInt(buffer);
        int length = ByteBufUtils.readVarInt(buffer);
        int[] passengers = new int[length];
        for (int i = 0; i < length; i++) {
            passengers[i] = ByteBufUtils.readVarInt(buffer);
        }
        return new SetPassengerMessage(entityId, passengers);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, SetPassengerMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getEntityId());
        ByteBufUtils.writeVarInt(buf, message.getPassengers().length);
        for (int passenger : message.getPassengers()) {
            ByteBufUtils.writeVarInt(buf, passenger);
        }
        return buf;
    }
}
