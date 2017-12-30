package net.glowstone.entity;

import com.flowpowered.network.Message;
import java.util.LinkedList;
import java.util.List;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;
import net.glowstone.net.message.play.entity.SpawnMobMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * Represents a creature entity such as a pig.
 */
public class GlowCreature extends GlowLivingEntity implements Creature {

    /**
     * The type of monster.
     */
    private final EntityType type;

    /**
     * The monster's target.
     */
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
    public EntityType getType() {
        return type;
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> result = new LinkedList<>();

        // spawn mob
        result.add(new SpawnMobMessage(
                id, getUniqueId(), type.getTypeId(), location, metadata.getEntryList()));

        // head facing
        result.add(new EntityHeadRotationMessage(id, Position.getIntYaw(location)));

        // todo: equipment
        //result.add(createEquipmentMessage());
        return result;
    }

    @Override
    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public void setTarget(LivingEntity target) {
        this.target = target;
    }
}
