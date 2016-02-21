package net.glowstone.entity;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;

import java.util.List;

/**
 * Represents a creature that ages, such as a sheep.
 */
public class GlowAgeable extends GlowCreature implements Ageable {

    private static final int AGE_BABY = -24000;
    private static final int AGE_ADULT = 0;
    private static final int BREEDING_AGE = 6000;
    protected float width, height;
    private int age = 0;
    private boolean ageLocked = false;
    private int forcedAge;
    private int inLove = 0;

    /**
     * Creates a new ageable monster.
     * @param location The location of the monster.
     * @param type The type of monster.
     */
    public GlowAgeable(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    @Override
    public void pulse() {
        super.pulse();
        if (this.ageLocked) {
            setScaleForAge(!isAdult());
        } else {
            int currentAge = this.age;
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
        return this.age;
    }

    @Override
    public final void setAge(int age) {
        this.age = age;
        this.setScaleForAge(isAdult());
    }

    @Override
    public final boolean getAgeLock() {
        return this.ageLocked;
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
        return this.age >= AGE_ADULT;
    }

    @Override
    public final boolean canBreed() {
        return this.age == AGE_ADULT;
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
        map.set(MetadataIndex.AGE_ISBABY, !this.isAdult());
        messages.add(new EntityMetadataMessage(id, map.getEntryList()));
        return messages;
    }

    protected final void setScale(float scale) {
        setSize(this.height * scale, this.width * scale);
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
}
