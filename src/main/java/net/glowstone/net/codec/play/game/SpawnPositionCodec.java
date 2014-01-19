package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.SpawnPositionMessage;

import java.io.IOException;

public final class SpawnPositionCodec implements Codec<SpawnPositionMessage> {

    @Override
    public SpawnPositionMessage decode(ByteBuf buffer) throws IOException {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();

        return new SpawnPositionMessage(x, y, z);
    }

    @Override
    public void encode(ByteBuf buf, SpawnPositionMessage message) throws IOException {
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());


    }
}
