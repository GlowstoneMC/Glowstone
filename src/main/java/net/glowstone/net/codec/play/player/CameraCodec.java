package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.CameraMessage;

public final class CameraCodec implements Codec<CameraMessage> {

    @Override
    public CameraMessage decode(ByteBuf buffer) throws IOException {
        int cameraId = ByteBufUtils.readVarInt(buffer);
        return new CameraMessage(cameraId);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CameraMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getCameraId());
        return buf;
    }
}
