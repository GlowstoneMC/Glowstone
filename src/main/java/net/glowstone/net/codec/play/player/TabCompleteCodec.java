package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.TabCompleteMessage;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class TabCompleteCodec implements Codec<TabCompleteMessage> {
    @Override
    public TabCompleteMessage decode(ByteBuf buf) throws IOException {
        String text = ByteBufUtils.readUTF8(buf);

        boolean hasLocation = buf.readBoolean();
        BlockVector location = null;
        if (hasLocation) {
            location = GlowBufUtils.readBlockPosition(buf);
        }
        return new TabCompleteMessage(text, location);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TabCompleteMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getText());
        final BlockVector location = message.getLocation();
        if (location != null) {
            buf.writeBoolean(true);
            GlowBufUtils.writeBlockPosition(buf, location);
        } else {
            buf.writeBoolean(false);
        }
        return buf;
    }
}
