package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerSwingArmMessage;

import java.io.IOException;

public final class PlayerSwingArmCodec implements Codec<PlayerSwingArmMessage> {
    @Override
    public PlayerSwingArmMessage decode(ByteBuf buf) throws IOException {
        int hand = ByteBufUtils.readVarInt(buf);
        return new PlayerSwingArmMessage(hand);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, PlayerSwingArmMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getHand());
        return buf;
    }
}
