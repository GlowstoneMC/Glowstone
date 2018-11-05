package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.RespawnMessage;

public final class RespawnCodec implements Codec<RespawnMessage> {

    @Override
    public RespawnMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int dimension = buf.readInt();
        int difficulty = buf.readByte();
        int mode = buf.readByte();
        String levelType = ByteBufUtils.readUTF8(buf);
        return new RespawnMessage(dimension, difficulty, mode, levelType);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, RespawnMessage message) throws IOException {
        buf.writeInt(message.getDimension());
        buf.writeByte(message.getDifficulty());
        buf.writeByte(message.getMode());
        ByteBufUtils.writeUTF8(buf, message.getLevelType());
        return buf;
    }
}
