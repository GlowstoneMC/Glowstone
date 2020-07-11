package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.QueryBlockNBTMessage;

import java.io.IOException;

public final class QueryBlockNBTCodec implements Codec<QueryBlockNBTMessage> {

    @Override
    public QueryBlockNBTMessage decode(ByteBuf byteBuf) throws IOException {
        int transactionID = ByteBufUtils.readVarInt(byteBuf);
        long position = byteBuf.readLong();
        int x = ((Long)(position >> 38)).intValue();
        int y = ((Long)((position >> 26) & 0xFFF)).intValue();
        int z = ((Long)(position << 38 >> 38)).intValue();
        return new QueryBlockNBTMessage(transactionID, x, y, z);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, QueryBlockNBTMessage message) {
        ByteBufUtils.writeVarInt(byteBuf, message.getTransactionID());
        long x = ((Integer) message.getX()).longValue();
        long y = ((Integer) message.getY()).longValue();
        long z = ((Integer) message.getZ()).longValue();
        byteBuf.writeLong(((x & 0x3FFFFFF) << 38) | ((z & 0x3FFFFFF) << 12) | (y & 0xFFF));
        return byteBuf;
    }
}
