package net.glowstone.entity;

import com.flowpowered.network.Message;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.inventory.GlowMetaSpawn;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.SoundUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents a creature that ages, such as a sheep.
 */
public class GlowAgeable extends GlowCreature implements Ageable {

    private static final int AGE_BABY = -24000;
    private static final int AGE_ADULT = 0;
    private static final int BREEDING_AGE = 6000;
    protected float width, height;
    private int age;
    private boolean ageLocked;
    private int forcedAge;
    private int inLove;
    private GlowAgeable parent;

    /**
     * Creates a new ageable creature.
     *
     * @param location  The location of the creature.
     * @param type      The type of monster.
     * @param maxHealth The max health of the creature.
     */
    public GlowAgeable(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    @Override
    public void pulse() {
        super.pulse();
        if (ageLocked) {
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
    public final int getAge() {
        return age;
    }

    @Override
    public final void setAge(int age) {
        this.age = age;
        setScaleForAge(isAdult());
    }

    @Override
    public final boolean getAgeLock() {
        return ageLocked;
    }

    @Override
    public final void setAgeLock(boolean ageLocked) {
        this.ageLocked = ageLocked;
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
        messages.add(new EntityMetadataMessage(id, map.getEntryList()));
        return messages;
    }

    protected final void setScale(float scale) {
        setSize(height * scale, width * scale);
    }

    public int getForcedAge() {
        return forcedAge;
    }

    public void setForcedAge(int forcedAge) {
        this.forcedAge = forcedAge;
    }

    public int getInLove() {
        return inLove;
    }

    public void setInLove(int inLove) {
        this.inLove = inLove;
    }


    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        super.entityInteract(player, message);
        ItemStack item = player.getItemInHand();

        // Spawn eggs are used to spawn babies
        if (item != null && item.getType() == Material.MONSTER_EGG && item.hasItemMeta()) {
            GlowMetaSpawn meta = (GlowMetaSpawn) item.getItemMeta();
            if (meta.hasSpawnedType() && meta.getSpawnedType() == this.getType()) {
                this.createBaby();

                if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                    // Consume the egg
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().clear(player.getInventory().getHeldItemSlot());
                    }
                }
                return true;
            }
        }
        return false;
    }

    public Ageable createBaby() {
        Class<? extends GlowEntity> spawn = EntityRegistry.getEntity(getType());
        GlowAgeable ageable = (GlowAgeable) getWorld().spawn(getLocation(), spawn, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
        ageable.setBaby();
        ageable.setParent(this);

        return ageable;
    }

    public Ageable getParent() {
        return parent;
    }

    public void setParent(Ageable parent) {
        this.parent = (GlowAgeable) parent;
    }

    @Override
    protected float getSoundPitch() {
        if (!isAdult()) {
            return SoundUtil.randomReal(0.2F) + 1.5F;
        }
        return super.getSoundPitch();
    }
}
