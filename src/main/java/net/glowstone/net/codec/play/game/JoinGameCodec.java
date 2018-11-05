package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.JoinGameMessage;

public final class JoinGameCodec implements Codec<JoinGameMessage> {

    @Override
    public JoinGameMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        int id = buffer.readInt();
        byte gameMode = buffer.readByte();
        int dimension = buffer.readInt();
        byte difficulty = buffer.readByte();
        byte maxPlayers = buffer.readByte();
        String levelType = ByteBufUtils.readUTF8(buffer);
        boolean reducedDebug = buffer.readBoolean();
        return new JoinGameMessage(id, gameMode, dimension, difficulty, maxPlayers, levelType,
            reducedDebug);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, JoinGameMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getMode());
        buf.writeInt(message.getDimension());
        buf.writeByte(message.getDifficulty());
        buf.writeByte(message.getMaxPlayers());
        ByteBufUtils.writeUTF8(buf, message.getLevelType());
        buf.writeBoolean(message.isReducedDebugInfo());
        return buf;
    }
}
