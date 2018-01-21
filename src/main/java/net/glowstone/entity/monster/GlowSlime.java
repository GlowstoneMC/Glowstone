package net.glowstone.entity.monster;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

// TODO: Split when killed
public class GlowSlime extends GlowMonster implements Slime {

    private boolean onGround;

    public GlowSlime(Location loc) {
        this(loc, EntityType.SLIME);
    }

    protected GlowSlime(Location loc, EntityType type) {
        super(loc, type, 1);
        int size = 1 + ThreadLocalRandom.current().nextInt(3);
        setMaxHealth(size * size); // max health = size^2
        setHealth(maxHealth); // reset health to max
        setBoundingBox(0.51000005 * size, 0.51000005 * size);
        setSize(size);
    }

    @Override
    public int getSize() {
        return metadata.getInt(MetadataIndex.SLIME_SIZE);
    }

    @Override
    public void setSize(int size) {
        checkArgument(size > 0);
        metadata.set(MetadataIndex.SLIME_SIZE, size);
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_SLIME_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SLIME_DEATH;
    }
}
