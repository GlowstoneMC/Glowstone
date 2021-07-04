package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DestroyEntitiesCodec implements Codec<DestroyEntitiesMessage> {

    @Override
    public DestroyEntitiesMessage decode(ByteBuf buf) throws IOException {
        int size = ByteBufUtils.readVarInt(buf);
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ids.add(ByteBufUtils.readVarInt(buf));
        }
        return new DestroyEntitiesMessage(ids);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, DestroyEntitiesMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getIds().size());
        for (int id : message.getIds()) {
            ByteBufUtils.writeVarInt(buf, id);
        }
        return buf;
    }
}
