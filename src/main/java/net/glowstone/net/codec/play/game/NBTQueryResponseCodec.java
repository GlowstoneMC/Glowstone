package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.NBTQueryResponseMessage;

import java.io.IOException;

public final class NBTQueryResponseCodec implements Codec<NBTQueryResponseMessage> {

    @Override
    public NBTQueryResponseMessage decode(ByteBuf byteBuf) throws IOException {
        int transactionID = ByteBufUtils.readVarInt(byteBuf);
        String nbt = ByteBufUtils.readUTF8(byteBuf);
        return new NBTQueryResponseMessage(transactionID, nbt);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, NBTQueryResponseMessage message) throws IOException {
        ByteBufUtils.writeVarInt(byteBuf, message.getTransactionID());
        ByteBufUtils.writeUTF8(byteBuf, message.getNbt());
        return byteBuf;
    }
}
