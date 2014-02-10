package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.TabCompleteMessage;

import java.io.IOException;

public final class TabCompleteCodec implements Codec<TabCompleteMessage> {
    public TabCompleteMessage decode(ByteBuf buf) throws IOException {
        String text = ByteBufUtils.readUTF8(buf);
        return new TabCompleteMessage(text);
    }

    public ByteBuf encode(ByteBuf buf, TabCompleteMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getText());
        return buf;
    }
}
