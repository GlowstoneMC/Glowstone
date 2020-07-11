package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.glowstone.net.message.play.game.QueryEntityNBTMessage;

import java.io.IOException;

public final class QueryEntityNBTCodec implements Codec<QueryEntityNBTMessage> {
    @Override
    public QueryEntityNBTMessage decode(ByteBuf byteBuf) throws IOException {
        int transactionID = ByteBufUtils.readVarInt(byteBuf);
        int entityID = ByteBufUtils.readVarInt(byteBuf);
        return new QueryEntityNBTMessage(transactionID, entityID);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, QueryEntityNBTMessage message) {
        ByteBufUtils.writeVarInt(byteBuf, message.getTransactionID());
        ByteBufUtils.writeVarInt(byteBuf, message.getEntityID());
        return byteBuf;
    }
}
