package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.chunk.ChunkSection;
import net.glowstone.chunk.SectionPosition;

import java.util.List;

@Data
public final class MultiBlockChangeMessage implements Message {

    private final SectionPosition sectionPosition;
    private final boolean suppressLightUpdates;
    private final List<BlockChangeMessage> records;

}
