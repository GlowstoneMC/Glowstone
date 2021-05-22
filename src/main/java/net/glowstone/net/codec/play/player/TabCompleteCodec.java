package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.TabCompleteMessage;

public final class TabCompleteCodec implements Codec<TabCompleteMessage> {

    @Override
    public TabCompleteMessage decode(ByteBuf buf) throws IOException {
        int transactionId = ByteBufUtils.readVarInt(buf);
        String text = ByteBufUtils.readUTF8(buf);
        return new TabCompleteMessage(transactionId, text);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TabCompleteMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getTransactionId());
        ByteBufUtils.writeUTF8(buf, message.getText());
        return buf;
    }
}
