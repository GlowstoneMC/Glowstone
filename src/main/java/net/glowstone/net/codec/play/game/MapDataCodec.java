package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.MapDataMessage;

import java.io.IOException;
import java.util.List;

public final class MapDataCodec implements Codec<MapDataMessage> {
    @Override
    public MapDataMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode MapDataMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, MapDataMessage message) throws IOException {
        final List<MapDataMessage.Icon> icons = message.getIcons();
        final MapDataMessage.Section section = message.getSection();

        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getScale());

        ByteBufUtils.writeVarInt(buf, icons.size());
        for (MapDataMessage.Icon icon : icons) {
            buf.writeByte((icon.facing << 4) | icon.type);
            buf.writeByte(icon.x);
            buf.writeByte(icon.y);
        }

        if (section == null) {
            buf.writeByte(0);
        } else {
            buf.writeByte(section.width);
            buf.writeByte(section.height);
            buf.writeByte(section.x);
            buf.writeByte(section.y);
            ByteBufUtils.writeVarInt(buf, section.data.length);
            buf.writeBytes(section.data);
        }

        return buf;
    }
}
