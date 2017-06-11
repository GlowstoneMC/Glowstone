package net.glowstone.advancement;

import com.flowpowered.network.util.ByteBufUtils;
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

    public GlowAdvancement(NamespacedKey key, GlowAdvancement parent) {
        this.key = key;
        this.parent = parent;
    }

    public GlowAdvancement(NamespacedKey key, GlowAdvancement parent, GlowAdvancementDisplay display) {
        this.key = key;
        this.parent = parent;
        this.display = display;
    }

    public void addCriterion(String criterion) {
        if (!criteriaIds.contains(criterion)) {
            criteriaIds.add(criterion);
        }
    }

    public void addRequirement(List<String> criteria) {
        requirements.add(criteria);
    }

    public List<String> getCriteria() {
        return criteriaIds;
    }

    public ByteBuf encode(ByteBuf buf) throws IOException {
        buf.writeBoolean(parent != null);
        if (parent != null) {
            ByteBufUtils.writeUTF8(buf, parent.getKey().toString());
        }
        buf.writeBoolean(display != null);
        if (display != null) {
            display.encode(buf);
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
