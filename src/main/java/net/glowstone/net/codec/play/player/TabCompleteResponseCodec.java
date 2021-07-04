package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.player.TabCompleteResponseMessage;

import java.io.IOException;

public final class TabCompleteResponseCodec implements Codec<TabCompleteResponseMessage> {

    @Override
    public TabCompleteResponseMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode TabCompleteResponseMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TabCompleteResponseMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getCompletions().size());
        for (String completion : message.getCompletions()) {
            ByteBufUtils.writeUTF8(buf, completion);
        }
        return buf;
    }
}
