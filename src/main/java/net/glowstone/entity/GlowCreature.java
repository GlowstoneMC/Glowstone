package net.glowstone.entity;

import com.destroystokyo.paper.entity.Pathfinder;
import com.flowpowered.network.Message;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;
import net.glowstone.net.message.play.entity.SpawnMobMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a creature entity such as a pig.
 */
public class GlowCreature extends GlowLivingEntity implements Creature {

    /**
     * The type of monster.
     */
    @Getter
    private final EntityType type;

    /**
     * The monster's target.
     */
    @Getter
    @Setter
    private LivingEntity target;

    /**
     * Creates a new monster.
     *
     * @param location The location of the monster.
     * @param type The type of monster.
     * @param maxHealth The max health of the monster.
     */
    public GlowCreature(Location location, EntityType type, double maxHealth) {
        super(location, maxHealth);
        this.type = type;
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> result = new LinkedList<>();

        // spawn mob
        result.add(new SpawnMobMessage(
                entityId, getUniqueId(), EntityNetworkUtil.getMobId(type),
                location, metadata.getEntryList()));

        // head facing
        result.add(new EntityHeadRotationMessage(entityId, Position.getIntYaw(location)));

        // todo: equipment
        //result.add(createEquipmentMessage());
        return result;
    }

    // TODO: 1.13
    @Override
    public @NotNull Pathfinder getPathfinder() {
        return null;
    }

    @Override
    public boolean isInDaylight() {
        return false;
    }

    @Override
    public void setLootTable(@Nullable LootTable table) {

    }

    @Override
    public @Nullable LootTable getLootTable() {
        return null;
    }

    @Override
    public void setSeed(long seed) {

    }

    @Override
    public long getSeed() {
        return 0;
    }
}
