package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.ResourcePackSendPacket;

import java.io.IOException;

public final class ResourcePackSendCodec implements Codec<ResourcePackSendPacket> {

    @Override
    public ResourcePackSendPacket decode(ByteBuf buffer) throws IOException {
        String url = ByteBufUtils.readUTF8(buffer);
        String hash = ByteBufUtils.readUTF8(buffer);
        return new ResourcePackSendPacket(url, hash);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ResourcePackSendPacket message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getUrl());
        ByteBufUtils.writeUTF8(buf, message.getHash());
        return buf;
    }
}
