package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.IOException;
import java.util.List;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.MapDataMessage;
import net.glowstone.net.message.play.game.MapDataMessage.Icon;
import net.glowstone.net.message.play.game.MapDataMessage.Section;

public final class MapDataCodec implements Codec<MapDataMessage> {

    @Override
    public MapDataMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode MapDataMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, MapDataMessage message) throws IOException {
        List<Icon> icons = message.getIcons();

        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getScale());

        ByteBufUtils.writeVarInt(buf, icons.size());
        for (Icon icon : icons) {
            buf.writeByte(icon.facing << 4 | icon.type);
            buf.writeByte(icon.x);
            buf.writeByte(icon.z);
            buf.writeByte(icon.direction);
            boolean hasDisplayName = icon.displayName != null;
            buf.writeBoolean(hasDisplayName);
            if (hasDisplayName) {
                GlowBufUtils.writeChat(buf, icon.displayName);
            }
        }
        Section section = message.getSection();
        if (section == null) {
            buf.writeByte(0);
        } else {
            buf.writeByte(section.width);
            buf.writeByte(section.height);
            buf.writeByte(section.x);
            buf.writeByte(section.z);
            ByteBufUtils.writeVarInt(buf, section.data.length);
            buf.writeBytes(section.data);
        }

        return buf;
    }
}
