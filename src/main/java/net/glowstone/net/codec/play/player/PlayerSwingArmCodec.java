package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.PlayerSwingArmMessage;

import java.io.IOException;

public final class PlayerSwingArmCodec implements Codec<PlayerSwingArmMessage> {
    public PlayerSwingArmMessage decode(ByteBuf buf) throws IOException {
        return new PlayerSwingArmMessage();
    }

    public ByteBuf encode(ByteBuf buf, PlayerSwingArmMessage message) throws IOException {
        return buf;
    }
}
