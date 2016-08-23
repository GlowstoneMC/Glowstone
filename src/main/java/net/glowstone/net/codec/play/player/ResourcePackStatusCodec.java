package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.ResourcePackStatusPacket;

import java.io.IOException;

public final class ResourcePackStatusCodec implements Codec<ResourcePackStatusPacket> {
    @Override
    public ResourcePackStatusPacket decode(ByteBuf buf) throws IOException {
        int result = ByteBufUtils.readVarInt(buf);
        return new ResourcePackStatusPacket(result);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ResourcePackStatusPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getResult());
        return buf;
    }
}
