package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.TeleportConfirmMessage;

public class TeleportConfirmCodec implements Codec<TeleportConfirmMessage> {

    @Override
    public TeleportConfirmMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        int id = ByteBufUtils.readVarInt(buffer);
        return new TeleportConfirmMessage(id);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, TeleportConfirmMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getTeleportId());
        return buf;
    }
}
