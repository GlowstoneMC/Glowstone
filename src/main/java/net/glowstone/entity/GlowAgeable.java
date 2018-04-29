package net.glowstone.entity;

import com.flowpowered.network.Message;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.inventory.GlowMetaSpawn;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.SoundUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a creature that ages, such as a sheep.
 */
public class GlowAgeable extends GlowCreature implements Ageable {

    private static final int AGE_BABY = -24000;
    private static final int AGE_ADULT = 0;
    private static final int BREEDING_AGE = 6000;
    private static final int MAX_GROW_AGE = -9 * 20;
    protected float width;
    protected float height;
    @Getter
    private int age;
    @Setter
    private boolean ageLock;
    @Getter
    @Setter
    private int forcedAge;
    @Getter
    @Setter
    private int inLove;
    @Getter
    @Setter
    private GlowAgeable parent;

    /**
     * Creates a new ageable creature.
     *
     * @param location The location of the creature.
     * @param type The type of monster.
     * @param maxHealth The max health of the creature.
     */
    public GlowAgeable(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    @Override
    public void pulse() {
        super.pulse();
        if (ageLock) {
            setScaleForAge(!isAdult());
        } else {
            int currentAge = age;
            if (currentAge < AGE_ADULT) {
                currentAge++;
                setAge(currentAge);
            } else if (currentAge > AGE_ADULT) {
                currentAge--;
                setAge(currentAge);
            }
        }
    }

    @Override
    public final void setAge(int age) {
        this.age = age;
        setScaleForAge(isAdult());
        metadata.set(MetadataIndex.AGE_ISBABY, !isAdult());
    }

    @Override
    public final boolean getAgeLock() {
        return ageLock;
    }

    @Override
    public final void setBaby() {
        if (isAdult()) {
            setAge(AGE_BABY);
        }
    }

    @Override
    public final void setAdult() {
        if (!isAdult()) {
            setAge(AGE_ADULT);
        }
    }

    @Override
    public final boolean isAdult() {
        return age >= AGE_ADULT;
    }

    @Override
    public boolean canBreed() {
        return age == AGE_ADULT;
    }

    /**
     * Gets whether this entity can grow when fed.
     *
     * @return true if this entity can grow when fed, false otherwise.
     */
    public boolean canGrow() {
        // feeding a baby has no effect if only 9 seconds remain
        return getAge() < MAX_GROW_AGE;
    }

    @Override
    public void setBreed(boolean breed) {
        if (breed) {
            setAge(AGE_ADULT);
        } else if (isAdult()) {
            setAge(BREEDING_AGE);
        }
    }

    public void setScaleForAge(boolean isAdult) {
        setScale(isAdult ? 1.0F : 0.5F);
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> messages = super.createSpawnMessage();
        MetadataMap map = new MetadataMap(GlowAgeable.class);
        map.set(MetadataIndex.AGE_ISBABY, !isAdult());
        messages.add(new EntityMetadataMessage(entityId, map.getEntryList()));
        return messages;
    }

    protected final void setScale(float scale) {
        setSize(height * scale, width * scale);
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (!super.entityInteract(player, message)
                && message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            ItemStack item = InventoryUtil
                .itemOrEmpty(player.getInventory().getItem(message.getHandSlot()));
            int growthAmount = computeGrowthAmount(item.getType());

            // Spawn eggs are used to spawn babies
            if (item.getType() == Material.MONSTER_EGG && item.hasItemMeta()) {
                GlowMetaSpawn meta = (GlowMetaSpawn) item.getItemMeta();
                if (meta.hasSpawnedType() && meta.getSpawnedType() == this.getType()) {
                    this.createBaby();

                    if (player.getGameMode() == GameMode.SURVIVAL
                        || player.getGameMode() == GameMode.ADVENTURE) {
                        player.getInventory().consumeItemInHand(message.getHandSlot());
                    }
                    return true;
                }
            } else if (growthAmount > 0) {
                grow(growthAmount);
                world.spawnParticle(Particle.VILLAGER_HAPPY, location, 5);
                player.getInventory().consumeItemInHand(message.getHandSlot());
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a baby clone of this creature, as when right-clicking it with a spawn egg.
     *
     * @return a baby clone of this creature
     */
    public Ageable createBaby() {
        Class<? extends GlowEntity> spawn = EntityRegistry.getEntity(getType());
        GlowAgeable ageable = (GlowAgeable) getWorld()
            .spawn(getLocation(), spawn, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
        ageable.setBaby();
        ageable.setParent(this);

        return ageable;
    }

    @Override
    protected float getSoundPitch() {
        if (!isAdult()) {
            return SoundUtil.randomReal(0.2F) + 1.5F;
        }
        return super.getSoundPitch();
    }

    /**
     * Grows an ageable creature.
     *
     * @param age The age to add to the ageable creature.
     */
    public void grow(int age) {
        setAge(this.age + age);
    }

    /**
     * Computes the growth amount using a specific material for the current ageable creature.
     * Always returns 0 for an adult or if the material is not food for the creature.
     *
     * @param material The food used to compute the growth amount.
     * @return The age gained using the given food.
     */
    protected int computeGrowthAmount(Material material) {
        return 0;
    }
}
