package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.CameraMessage;

import java.io.IOException;

public final class CameraCodec implements Codec<CameraMessage> {

    @Override
    public CameraMessage decode(ByteBuf buffer) throws IOException {
        int cameraID = ByteBufUtils.readVarInt(buffer);
        return new CameraMessage(cameraID);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CameraMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getCameraId());
        return buf;
    }
}
