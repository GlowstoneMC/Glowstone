package net.glowstone.advancement;

import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.advancement.AdvancementProgress;

import java.io.IOException;
import java.util.*;

@Data
public class GlowAdvancementProgress implements AdvancementProgress {

    private final GlowAdvancement advancement;
    private final GlowPlayer player;
    private final HashMap<String, Date> awarded = new HashMap<>();

    @Override
    public boolean isDone() {
        return getRemainingCriteria().size() == 0;
    }

    @Override
    public boolean awardCriteria(String criteria) {
        if (awarded.containsKey(criteria)) {
            return false; // already awarded
        }
        awarded.put(criteria, new Date());
        player.getAdvancementTracker().sendUpdate();
        return true;
    }

    @Override
    public boolean revokeCriteria(String criteria) {
        if (!awarded.containsKey(criteria)) {
            return false; // not awarded
        }
        awarded.remove(criteria);
        player.getAdvancementTracker().sendUpdate();
        return true;
    }

    @Override
    public Date getDateAwarded(String criteria) {
        if (!advancement.getCriteriaIds().contains(criteria)) {
            return null; // invalid criteria
        }
        return awarded.get(criteria);
    }

    @Override
    public Collection<String> getRemainingCriteria() {
        List<String> remaining = new ArrayList<>(advancement.getCriteriaIds());
        remaining.removeAll(awarded.keySet());
        return Collections.unmodifiableCollection(remaining);
    }

    @Override
    public Collection<String> getAwardedCriteria() {
        return Collections.unmodifiableCollection(awarded.keySet());
    }

    public ByteBuf encode(ByteBuf buf) throws IOException {
        List<String> criteriaIds = advancement.getCriteriaIds();
        ByteBufUtils.writeVarInt(buf, criteriaIds.size());
        for (String criterion : criteriaIds) {
            ByteBufUtils.writeUTF8(buf, criterion);
            boolean achieved = awarded.containsKey(criterion);
            buf.writeBoolean(achieved);
            if (achieved) {
                buf.writeLong(getDateAwarded(criterion).getTime());
            }
        }
        return buf;
    }
}
