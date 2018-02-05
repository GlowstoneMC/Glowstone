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
    public final boolean canBreed() {
        return age == AGE_ADULT;
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
        super.entityInteract(player, message);
        if (message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            ItemStack item = InventoryUtil
                .itemOrEmpty(player.getInventory().getItem(message.getHandSlot()));

            // Spawn eggs are used to spawn babies
            if (item.getType() == Material.MONSTER_EGG && item.hasItemMeta()) {
                GlowMetaSpawn meta = (GlowMetaSpawn) item.getItemMeta();
                if (meta.hasSpawnedType() && meta.getSpawnedType() == this.getType()) {
                    this.createBaby();

                    if (player.getGameMode() == GameMode.SURVIVAL
                        || player.getGameMode() == GameMode.ADVENTURE) {
                        // Consume the egg
                        if (item.getAmount() > 1) {
                            item.setAmount(item.getAmount() - 1);
                        } else {
                            player.getInventory()
                                .setItem(message.getHandSlot(), InventoryUtil.createEmptyStack());
                        }
                    }
                    return true;
                }
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
}
