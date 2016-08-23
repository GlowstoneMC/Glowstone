package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.CameraPacket;

import java.io.IOException;

public final class CameraCodec implements Codec<CameraPacket> {

    @Override
    public CameraPacket decode(ByteBuf buffer) throws IOException {
        int cameraID = ByteBufUtils.readVarInt(buffer);
        return new CameraPacket(cameraID);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CameraPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getCameraId());
        return buf;
    }
}
