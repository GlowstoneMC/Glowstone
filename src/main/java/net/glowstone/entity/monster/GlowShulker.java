package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.util.BlockVector;

public class GlowShulker extends GlowMonster implements Shulker {

    private Facing direction;
    private byte shieldHeight;
    private Location attachment;

    public GlowShulker(Location loc) {
        super(loc, EntityType.SHULKER, 30);
        setDirection(Facing.DOWN); // todo
        setShieldHeight((byte) 0);
        setAttachment(null); // todo
    }

    public Facing getFacingDirection() {
        return direction;
    }

    public void setDirection(Facing direction) {
        this.direction = direction;
        this.metadata.set(MetadataIndex.SHULKER_FACING_DIRECTION, direction.ordinal());
    }

    public byte getShieldHeight() {
        return shieldHeight;
    }

    public void setShieldHeight(byte shieldHeight) {
        this.shieldHeight = shieldHeight;
        this.metadata.set(MetadataIndex.SHULKER_SHIELD_HEIGHT, shieldHeight);
    }

    public Location getAttachment() {
        return attachment;
    }

    public void setAttachment(Location attachment) {
        this.attachment = attachment;
        if (attachment != null) {
            this.metadata.set(MetadataIndex.SHULKER_ATTACHMENT_POSITION, new BlockVector(attachment.toVector()));
        } else {
            this.metadata.set(MetadataIndex.SHULKER_ATTACHMENT_POSITION, null);
        }
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SHULKER_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        if (shieldHeight == 0) {
            return Sound.ENTITY_SHULKER_HURT_CLOSED;
        }
        return Sound.ENTITY_SHULKER_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_SHULKER_AMBIENT;
    }

    public enum Facing {
        DOWN, UP, NORTH, SOUTH, WEST, EAST
    }
}
