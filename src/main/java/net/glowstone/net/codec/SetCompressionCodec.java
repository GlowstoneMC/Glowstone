package net.glowstone.net.codec;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.SetCompressionMessage;

public final class SetCompressionCodec implements Codec<SetCompressionMessage> {

    @Override
    public SetCompressionMessage decode(ByteBuf buf) throws IOException {
        int threshold = ByteBufUtils.readVarInt(buf);
        return new SetCompressionMessage(threshold);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SetCompressionMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getThreshold());
        return buf;
    }
}
