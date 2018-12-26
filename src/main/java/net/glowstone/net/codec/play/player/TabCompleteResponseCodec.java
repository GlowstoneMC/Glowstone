package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.TabCompleteResponseMessage;

public final class TabCompleteResponseCodec implements Codec<TabCompleteResponseMessage> {

    @Override
    public TabCompleteResponseMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode TabCompleteResponseMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TabCompleteResponseMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getTransactionId());
        ByteBufUtils.writeVarInt(buf, message.getStartIndex());
        ByteBufUtils.writeVarInt(buf, message.getTextLength());
        ByteBufUtils.writeVarInt(buf, message.getCompletions().size());
        for (TabCompleteResponseMessage.Completion completion : message.getCompletions()) {
            ByteBufUtils.writeUTF8(buf, completion.getMatch());
            boolean hasToolip = completion.getTooltip() != null;
            buf.writeBoolean(hasToolip);
            if (hasToolip) {
                GlowBufUtils.writeChat(buf, completion.getTooltip());
            }
        }
        return buf;
    }
}
