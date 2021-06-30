package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.NameItemMessage;

public final class NameItemCodec implements Codec<NameItemMessage> {
    @Override
    public NameItemMessage decode(ByteBuf buf) throws IOException {
        String name = ByteBufUtils.readUTF8(buf);
        return new NameItemMessage(name);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, NameItemMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getName());
        return buf;
    }
}
