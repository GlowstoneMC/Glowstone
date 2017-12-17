package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.UseItemMessage;

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
