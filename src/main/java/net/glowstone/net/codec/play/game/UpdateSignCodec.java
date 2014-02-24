package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.UpdateSignMessage;

import java.io.IOException;

public final class UpdateSignCodec implements Codec<UpdateSignMessage> {
    public UpdateSignMessage decode(ByteBuf buf) throws IOException {
        int x = buf.readInt();
        int y = buf.readShort();
        int z = buf.readInt();
        String[] message = new String[4];
        for (int i = 0; i < message.length; ++i) {
            message[i] = ByteBufUtils.readUTF8(buf);
        }
        return new UpdateSignMessage(x, y, z, message);
    }

    public ByteBuf encode(ByteBuf buf, UpdateSignMessage message) throws IOException {
        buf.writeInt(message.getX());
        buf.writeShort(message.getY());
        buf.writeInt(message.getZ());
        for (String line : message.getMessage()) {
            ByteBufUtils.writeUTF8(buf, line);
        }
        return buf;
    }
}
