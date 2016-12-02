package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.util.BlockVector;

public class GlowShulker extends GlowMonster implements Shulker {

    private Facing direction;
    private byte height;
    private Location attachment;

    public GlowShulker(Location loc) {
        super(loc, EntityType.SHULKER, 30);
        setDirection(Facing.DOWN); // todo
        setHeight((byte) 0);
        setAttachment(null); // todo
    }

    public Facing getFacingDirection() {
        return direction;
    }

    public void setDirection(Facing direction) {
        this.direction = direction;
        this.metadata.set(MetadataIndex.SHULKER_FACING_DIRECTION, direction.ordinal());
    }

    public byte getHeight() {
        return height;
    }

    public void setHeight(byte height) {
        this.height = height;
        this.metadata.set(MetadataIndex.SHULKER_SHIELD_HEIGHT, height);
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
        if (height == 0) {
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
