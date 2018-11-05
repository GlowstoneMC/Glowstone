package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.PlayerSwingArmMessage;

public final class PlayerSwingArmCodec implements Codec<PlayerSwingArmMessage> {

    @Override
    public PlayerSwingArmMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int hand = ByteBufUtils.readVarInt(buf);
        return new PlayerSwingArmMessage(hand);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, PlayerSwingArmMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getHand());
        return buf;
    }
}
