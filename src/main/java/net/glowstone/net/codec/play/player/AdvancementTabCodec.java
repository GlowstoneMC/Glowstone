package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.AdvancementTabMessage;

import java.io.IOException;

public class AdvancementTabCodec implements Codec<AdvancementTabMessage> {

    @Override
    public AdvancementTabMessage decode(ByteBuf buf) throws IOException {
        int action = ByteBufUtils.readVarInt(buf);
        if (action == AdvancementTabMessage.ACTION_CLOSE) {
            return new AdvancementTabMessage();
        }
        String tabId = ByteBufUtils.readUTF8(buf);
        return new AdvancementTabMessage(action, tabId);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, AdvancementTabMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getAction());
        if (message.getAction() == AdvancementTabMessage.ACTION_OPEN) {
            ByteBufUtils.writeUTF8(buf, message.getTabId());
        }
        return buf;
    }
}
