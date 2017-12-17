package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;

@Data
public class AdvancementsMessage implements Message {

    private final boolean clear;
    private final Map<NamespacedKey, Advancement> advancements;
    private final List<NamespacedKey> removeAdvancements;
    // todo: progress
}
