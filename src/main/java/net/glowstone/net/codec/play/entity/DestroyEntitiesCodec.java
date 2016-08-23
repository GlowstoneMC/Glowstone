package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.DestroyEntitiesPacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DestroyEntitiesCodec implements Codec<DestroyEntitiesPacket> {
    @Override
    public DestroyEntitiesPacket decode(ByteBuf buf) throws IOException {
        int size = ByteBufUtils.readVarInt(buf);
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ids.add(ByteBufUtils.readVarInt(buf));
        }
        return new DestroyEntitiesPacket(ids);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, DestroyEntitiesPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getIds().size());
        for (int id : message.getIds()) {
            ByteBufUtils.writeVarInt(buf, id);
        }
        return buf;
    }
}
