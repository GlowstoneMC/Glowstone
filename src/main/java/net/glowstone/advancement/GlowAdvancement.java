package net.glowstone.advancement;

import com.flowpowered.network.util.ByteBufUtils;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class GlowAdvancement implements Advancement {

    private final NamespacedKey key;
    private final GlowAdvancement parent;
    private final List<String> criteriaIds = new ArrayList<>();
    private final List<List<String>> requirements = new ArrayList<>();
    private GlowAdvancementDisplay display = null;

    /**
     * Creates an advancement with the default notification.
     * @param key the namespace and name of the advancement
     * @param parent the prerequisite advancement, or null
     */
    public GlowAdvancement(NamespacedKey key, GlowAdvancement parent) {
        this.key = key;
        this.parent = parent;
    }

    /**
     * Creates an advancement.
     * @param key the namespace and name of the advancement
     * @param parent the prerequisite advancement, or null for no prerequisite
     * @param display the parameters for the notification when this advancement is earned, or null
     *     for the default notification
     */
    public GlowAdvancement(NamespacedKey key, GlowAdvancement parent,
        GlowAdvancementDisplay display) {
        this.key = key;
        this.parent = parent;
        this.display = display;
    }

    /**
     * Adds a criterion.
     * @param criterion TODO: document where this ID comes from
     */
    public void addCriterion(String criterion) {
        if (!criteriaIds.contains(criterion)) {
            criteriaIds.add(criterion);
        }
    }

    public void addRequirement(List<String> criteria) {
        requirements.add(criteria);
    }

    @Override
    public List<String> getCriteria() {
        return ImmutableList.copyOf(criteriaIds);
    }

    /**
     * Writes a notification of earning this advancement to a byte buffer.
     * @param buf a {@link ByteBuf}
     * @return {@code buf} with this advancement written to it
     * @throws IOException if a string is too long
     */
    public ByteBuf encode(ByteBuf buf) throws IOException {
        buf.writeBoolean(parent != null);
        if (parent != null) {
            ByteBufUtils.writeUTF8(buf, parent.getKey().toString());
        }
        buf.writeBoolean(display != null);
        if (display != null) {
            display.encode(buf, true, true, false);
        }
        ByteBufUtils.writeVarInt(buf, criteriaIds.size());
        for (String criteriaId : criteriaIds) {
            ByteBufUtils.writeUTF8(buf, criteriaId);
        }
        ByteBufUtils.writeVarInt(buf, requirements.size());
        for (List<String> requirement : requirements) {
            ByteBufUtils.writeVarInt(buf, requirement.size());
            for (String criterion : requirement) {
                ByteBufUtils.writeUTF8(buf, criterion);
            }
        }
        return buf;
    }
}
