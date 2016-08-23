package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.JoinGamePacket;

import java.io.IOException;

public final class JoinGameLegacyCodec implements Codec<JoinGamePacket> {
    @Override
    public JoinGamePacket decode(ByteBuf buffer) throws IOException {
        int id = buffer.readInt();
        byte gameMode = buffer.readByte();
        byte dimension = buffer.readByte();
        byte difficulty = buffer.readByte();
        byte maxPlayers = buffer.readByte();
        String levelType = ByteBufUtils.readUTF8(buffer);
        boolean reducedDebug = buffer.readBoolean();
        return new JoinGamePacket(id, gameMode, dimension, difficulty, maxPlayers, levelType, reducedDebug);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, JoinGamePacket message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getMode());
        buf.writeByte(message.getDimension());
        buf.writeByte(message.getDifficulty());
        buf.writeByte(message.getMaxPlayers());
        ByteBufUtils.writeUTF8(buf, message.getLevelType());
        buf.writeBoolean(message.isReducedDebugInfo());
        return buf;
    }
}
