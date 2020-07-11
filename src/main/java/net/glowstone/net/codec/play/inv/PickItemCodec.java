package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.inv.PickItemMessage;

import java.io.IOException;

public final class PickItemCodec implements Codec<PickItemMessage> {
    @Override
    public PickItemMessage decode(ByteBuf byteBuf) throws IOException {
        int slotToUse = ByteBufUtils.readVarInt(byteBuf);
        return new PickItemMessage(slotToUse);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, PickItemMessage message) {
        ByteBufUtils.writeVarInt(byteBuf, message.getSlotToUse());
        return byteBuf;
    }
}
