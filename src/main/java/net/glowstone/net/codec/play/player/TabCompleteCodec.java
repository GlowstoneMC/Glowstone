package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.TabCompletePacket;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class TabCompleteCodec implements Codec<TabCompletePacket> {
    @Override
    public TabCompletePacket decode(ByteBuf buf) throws IOException {
        String text = ByteBufUtils.readUTF8(buf);
        boolean assumeCommand = buf.readBoolean();
        boolean hasLocation = buf.readBoolean();
        BlockVector location = null;
        if (hasLocation) {
            location = GlowBufUtils.readBlockPosition(buf);
        }
        return new TabCompletePacket(text, assumeCommand, location);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TabCompletePacket message) throws IOException {
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
