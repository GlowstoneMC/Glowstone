package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.ResourcePackStatusMessage;

import java.io.IOException;

public final class ResourcePackStatusCodec implements Codec<ResourcePackStatusMessage> {

    @Override
    public ResourcePackStatusMessage decode(ByteBuf buf) throws IOException {
        int result = ByteBufUtils.readVarInt(buf);
        return new ResourcePackStatusMessage(result);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ResourcePackStatusMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getResult());
        return buf;
    }
}
