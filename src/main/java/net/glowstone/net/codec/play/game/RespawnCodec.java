package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.RespawnPacket;

import java.io.IOException;

public final class RespawnCodec implements Codec<RespawnPacket> {
    @Override
    public RespawnPacket decode(ByteBuf buf) throws IOException {
        int dimension = buf.readInt();
        int difficulty = buf.readByte();
        int mode = buf.readByte();
        String levelType = ByteBufUtils.readUTF8(buf);
        return new RespawnPacket(dimension, difficulty, mode, levelType);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, RespawnPacket message) throws IOException {
        buf.writeInt(message.getDimension());
        buf.writeByte(message.getDifficulty());
        buf.writeByte(message.getMode());
        ByteBufUtils.writeUTF8(buf, message.getLevelType());
        return buf;
    }
}
