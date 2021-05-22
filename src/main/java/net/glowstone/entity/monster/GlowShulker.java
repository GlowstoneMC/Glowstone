package net.glowstone.entity.monster;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.util.BlockVector;

public class GlowShulker extends GlowMonster implements Shulker {

    @Getter
    private Location attachment;

    @Getter
    @Setter
    private float peek;

    @Getter
    @Setter
    private BlockFace attachedFace;

    /**
     * Creates a shulker facing down.
     *
     * @param loc the location
     */
    public GlowShulker(Location loc) {
        this(loc, Facing.DOWN);
    }

    /**
     * Creates a shulker facing the given direction.
     *
     * @param loc       the location
     * @param direction the direction to initially face
     */
    public GlowShulker(Location loc, Facing direction) {
        super(loc, EntityType.SHULKER, 30);
        setDirection(direction);
        setShieldHeight((byte) 0);
        setAttachment(loc);
    }

    public Facing getFacingDirection() {
        return Facing.values()[metadata.getByte(MetadataIndex.SHULKER_FACING_DIRECTION)];
    }

    public void setDirection(Facing direction) {
        this.metadata.set(MetadataIndex.SHULKER_FACING_DIRECTION, direction.ordinal());
    }

    public byte getShieldHeight() {
        return metadata.getByte(MetadataIndex.SHULKER_SHIELD_HEIGHT);
    }

    public void setShieldHeight(byte shieldHeight) {
        this.metadata.set(MetadataIndex.SHULKER_SHIELD_HEIGHT, shieldHeight);
    }

    /**
     * Sets the point where this shulker is attached, or null to detach the shulker.
     *
     * @param attachment the new attachment point, or null to detach
     */
    public void setAttachment(Location attachment) {
        this.attachment = attachment;
        if (attachment != null) {
            this.metadata.set(MetadataIndex.SHULKER_ATTACHMENT_POSITION,
                new BlockVector(attachment.toVector()));
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
        if (getShieldHeight() == 0) {
            return Sound.ENTITY_SHULKER_HURT_CLOSED;
        }
        return Sound.ENTITY_SHULKER_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_SHULKER_AMBIENT;
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.getByWoolData(metadata.getByte(MetadataIndex.SHULKER_COLOR));
    }

    @Override
    public void setColor(DyeColor color) {
        metadata.set(MetadataIndex.SHULKER_COLOR, color.getWoolData());
    }

    public enum Facing {
        DOWN, UP, NORTH, SOUTH, WEST, EAST
    }
}
