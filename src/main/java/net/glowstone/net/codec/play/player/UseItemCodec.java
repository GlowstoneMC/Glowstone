package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.handler.play.player.UseItemMessage;

import java.io.IOException;

public class UseItemCodec implements Codec<UseItemMessage> {
    @Override
    public UseItemMessage decode(ByteBuf buffer) throws IOException {
        int hand = ByteBufUtils.readVarInt(buffer);
        return new UseItemMessage(hand);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UseItemMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getHand());
        return buf;
    }
}
