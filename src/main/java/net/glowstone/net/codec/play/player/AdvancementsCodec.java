package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.advancement.GlowAdvancement;
import net.glowstone.net.message.play.player.AdvancementsMessage;
import org.bukkit.NamespacedKey;

public class AdvancementsCodec implements Codec<AdvancementsMessage> {

    @Override
    public AdvancementsMessage decode(ByteBuf buf) throws IOException {
        throw new UnsupportedOperationException("Cannot decode AdvancementsMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, AdvancementsMessage message) throws IOException {
        buf.writeBoolean(message.isClear());
        ByteBufUtils.writeVarInt(buf, message.getAdvancements().size());
        for (NamespacedKey key : message.getAdvancements().keySet()) {
            ByteBufUtils.writeUTF8(buf, key.toString());
            GlowAdvancement advancement = (GlowAdvancement) message.getAdvancements().get(key);
            advancement.encode(buf);
        }
        ByteBufUtils.writeVarInt(buf, message.getRemoveAdvancements().size());
        for (NamespacedKey key : message.getRemoveAdvancements()) {
            ByteBufUtils.writeUTF8(buf, key.toString());
        }
        // todo: progress
        ByteBufUtils.writeVarInt(buf, 0);
        return buf;
    }
}
