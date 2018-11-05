package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.ResourcePackStatusMessage;

public final class ResourcePackStatusCodec implements Codec<ResourcePackStatusMessage> {

    @Override
    public ResourcePackStatusMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int result = ByteBufUtils.readVarInt(buf);
        return new ResourcePackStatusMessage(result);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, ResourcePackStatusMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getResult());
        return buf;
    }
}
