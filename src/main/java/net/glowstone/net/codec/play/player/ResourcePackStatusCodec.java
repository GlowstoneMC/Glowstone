package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.ResourcePackStatusMessage;

import java.io.IOException;

public final class ResourcePackStatusCodec implements Codec<ResourcePackStatusMessage> {
    @Override
    public ResourcePackStatusMessage decode(ByteBuf buf) throws IOException {
        String hash = ByteBufUtils.readUTF8(buf);
        int result = ByteBufUtils.readVarInt(buf);
        return new ResourcePackStatusMessage(hash, result);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ResourcePackStatusMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getHash());
        ByteBufUtils.writeVarInt(buf, message.getResult());
        return buf;
    }
}
