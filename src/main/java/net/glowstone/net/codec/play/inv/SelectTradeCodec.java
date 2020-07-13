package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.inv.SelectTradeMessage;

import java.io.IOException;

public final class SelectTradeCodec implements Codec<SelectTradeMessage> {
    @Override
    public SelectTradeMessage decode(ByteBuf byteBuf) throws IOException {
        int selectedSlot = ByteBufUtils.readVarInt(byteBuf);
        return new SelectTradeMessage(selectedSlot);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, SelectTradeMessage message) {
        ByteBufUtils.writeVarInt(byteBuf, message.getSelectedSlot());
        return byteBuf;
    }
}
