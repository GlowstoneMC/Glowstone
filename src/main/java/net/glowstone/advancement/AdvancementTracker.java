package net.glowstone.advancement;

import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.player.AdvancementsMessage;
import org.bukkit.advancement.Advancement;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

@Data
public class AdvancementTracker {

    private final GlowPlayer player;
    private final HashMap<Advancement, GlowAdvancementProgress> progress;

    public void sendUpdate() {
        // do NOT call this before sending the Advancements definition packet!
        AdvancementsMessage message = new AdvancementsMessage(false, Collections.emptyMap(), Collections.emptyList(), this);
        player.getSession().send(message);
    }

    public GlowAdvancementProgress getProgress(Advancement advancement) {
        if (progress.containsKey(advancement)) {
            return progress.get(advancement);
        }
        progress.put(advancement, new GlowAdvancementProgress((GlowAdvancement) advancement, player));
        return getProgress(advancement);
    }

    public ByteBuf encode(ByteBuf buf) throws IOException {
        ByteBufUtils.writeVarInt(buf, progress.size());
        for (Advancement advancement : progress.keySet()) {
            ByteBufUtils.writeUTF8(buf, advancement.getKey().toString());
            GlowAdvancementProgress progress = this.progress.get(advancement);
            progress.encode(buf);
        }
        return buf;
    }
}
