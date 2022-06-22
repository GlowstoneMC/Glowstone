package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.AcknowledgeBlockChanges;

import java.io.IOException;

public class AcknowledgeBlockChangesCodec implements Codec<AcknowledgeBlockChanges> {
    @Override
    public AcknowledgeBlockChanges decode(ByteBuf buf) throws IOException {
        int sequenceId = ByteBufUtils.readVarInt(buf);
        return new AcknowledgeBlockChanges(sequenceId);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, AcknowledgeBlockChanges acknowledgeBlockChanges) throws IOException {
        ByteBufUtils.writeVarInt(buf, acknowledgeBlockChanges.getSequenceId());
        return buf;
    }
}
