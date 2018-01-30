package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.TabCompleteMessage;
import org.bukkit.util.BlockVector;

public final class TabCompleteCodec implements Codec<TabCompleteMessage> {

    @Override
    public TabCompleteMessage decode(ByteBuf buf) throws IOException {
        String text = ByteBufUtils.readUTF8(buf);
        boolean assumeCommand = buf.readBoolean();
        boolean hasLocation = buf.readBoolean();
        BlockVector location = null;
        if (hasLocation) {
            location = GlowBufUtils.readBlockPosition(buf);
        }
        return new TabCompleteMessage(text, assumeCommand, location);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TabCompleteMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getText());
        buf.writeBoolean(message.isAssumeCommand());
        BlockVector location = message.getLocation();
        if (location != null) {
            buf.writeBoolean(true);
            GlowBufUtils.writeBlockPosition(buf, location);
        } else {
            buf.writeBoolean(false);
        }
        return buf;
    }
}
