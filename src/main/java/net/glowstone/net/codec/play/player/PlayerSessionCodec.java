package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.PlayerSessionMessage;

import java.io.IOException;
import java.util.UUID;

public final class PlayerSessionCodec implements Codec<PlayerSessionMessage> {
    @Override
    public PlayerSessionMessage decode(ByteBuf byteBuf) throws IOException {
        UUID uuid = GlowBufUtils.readUuid(byteBuf);
        long expires = byteBuf.readLong();
        int pubKeyLen = ByteBufUtils.readVarInt(byteBuf);
        ByteBuf pubKey = byteBuf.readBytes(pubKeyLen);

        int pubKeySigLen = ByteBufUtils.readVarInt(byteBuf);
        ByteBuf pubKeySig = byteBuf.readBytes(pubKeySigLen);
        return new PlayerSessionMessage(uuid, expires, pubKey, pubKeySig);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, PlayerSessionMessage playerSessionMessage) throws IOException {
        throw new RuntimeException("Can't encode PlayerSessionMessage");
    }
}
