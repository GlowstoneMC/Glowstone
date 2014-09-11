package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.JoinGameMessage;

import java.io.IOException;

public final class JoinGameCodec implements Codec<JoinGameMessage> {
    @Override
    public JoinGameMessage decode(ByteBuf buffer) throws IOException {
        int id = buffer.readInt();
        byte gameMode = buffer.readByte();
        byte dimension = buffer.readByte();
        byte difficulty = buffer.readByte();
        byte maxPlayers = buffer.readByte();
        String levelType = ByteBufUtils.readUTF8(buffer);
        boolean reducedDebug = buffer.readBoolean();
        return new JoinGameMessage(id, gameMode, dimension, difficulty, maxPlayers, levelType, reducedDebug);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, JoinGameMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getGameMode());
        buf.writeByte(message.getDimension());
        buf.writeByte(message.getDifficulty());
        buf.writeByte(message.getMaxPlayers());
        ByteBufUtils.writeUTF8(buf, message.getLevelType());
        buf.writeBoolean(message.getReducedDebugInfo());
        return buf;
    }
}
