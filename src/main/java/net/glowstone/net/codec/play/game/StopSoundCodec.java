package net.glowstone.net.codec.play.game;

import static com.flowpowered.network.util.ByteBufUtils.readUTF8;
import static com.flowpowered.network.util.ByteBufUtils.readVarInt;
import static com.flowpowered.network.util.ByteBufUtils.writeUTF8;
import static com.flowpowered.network.util.ByteBufUtils.writeVarInt;

import com.flowpowered.network.Codec;
import com.google.common.collect.ImmutableBiMap;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.StopSoundMessage;
import org.bukkit.SoundCategory;

public final class StopSoundCodec implements Codec<StopSoundMessage> {

    private static final ImmutableBiMap<SoundCategory, Integer> SOURCE_ID_MAP =
        ImmutableBiMap.<SoundCategory, Integer>builder()
            .put(SoundCategory.MASTER, 0)
            .put(SoundCategory.MUSIC, 1)
            .put(SoundCategory.RECORDS, 2)
            .put(SoundCategory.WEATHER, 3)
            .put(SoundCategory.BLOCKS, 4)
            .put(SoundCategory.HOSTILE, 5)
            .put(SoundCategory.NEUTRAL, 6)
            .put(SoundCategory.PLAYERS, 7)
            .put(SoundCategory.AMBIENT, 8)
            .put(SoundCategory.VOICE, 9)
            .build();

    private static final int FLAG_SOURCE = 0x01;
    private static final int FLAG_SOUND = 0x02;

    @Override
    public StopSoundMessage decode(ByteBuf buf) throws IOException {
        byte flags = buf.readByte();
        SoundCategory source =
            ((flags & FLAG_SOURCE) != 0) ? SOURCE_ID_MAP.inverse().get(readVarInt(buf)) : null;
        String sound = ((flags & FLAG_SOUND) != 0) ? readUTF8(buf) : null;
        return new StopSoundMessage(source, sound);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, StopSoundMessage message) throws IOException {
        byte flags = 0;
        if (message.getSource() != null) {
            flags |= FLAG_SOURCE;
        }
        if (message.getSound() != null) {
            flags |= FLAG_SOUND;
        }

        buf.writeByte(flags);
        if (message.getSource() != null) {
            writeVarInt(buf, SOURCE_ID_MAP.get(message.getSource()));
        }
        if (message.getSound() != null) {
            writeUTF8(buf, message.getSound());
        }
        return buf;
    }
}
