package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.handler.play.player.UseItemPacket;

import java.io.IOException;

public class UseItemCodec implements Codec<UseItemPacket> {
    @Override
    public UseItemPacket decode(ByteBuf buffer) throws IOException {
        int hand = ByteBufUtils.readVarInt(buffer);
        return new UseItemPacket(hand);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UseItemPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getHand());
        return buf;
    }
}
