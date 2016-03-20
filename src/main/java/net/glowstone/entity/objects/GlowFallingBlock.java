package net.glowstone.entity.objects;

import com.flowpowered.network.Message;

import net.glowstone.entity.GlowEntity;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.util.Position;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class GlowFallingBlock extends GlowEntity implements FallingBlock {

    /**
     * Velocity reduction applied each tick.
     */
    private static final double AIR_DRAG = 0.99;

    /**
     * Velocity reduction applied each tick.
     */
    private static final double LIQUID_DRAG = 0.8;

    /**
     * Gravity acceleration applied each tick.
     */
    private static final Vector GRAVITY = new Vector(0, -0.10, 0);

    Material material;
    boolean canHurtEntities;
    boolean dropItem;
    byte blockData;


    public GlowFallingBlock(Location location, Material material) {
        super(location);
        setBoundingBox(1.00, 1.00);
        this.material = material;

        dropItem = true;
        canHurtEntities = true;
        blockData = (byte) 0;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public boolean getDropItem() {
        return dropItem;
    }

    @Override
    public void setDropItem(boolean dropItem) {
        this.dropItem = dropItem;
    }

    @Override
    public boolean canHurtEntities() {
        return canHurtEntities;
    }

    @Override
    public void setHurtEntities(boolean canHurtEntities) {
        this.canHurtEntities = canHurtEntities;
    }

    @Override
    public byte getBlockData() {
        return blockData;
    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public void setGlowing(boolean isGlowing) {

    }

    @Override
    public int getBlockId() {
        return material.getId();
    }

    public Location getSourceLoc() {
        return location;
    }

    @Override
    public List<Message> createSpawnMessage() {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        return Arrays.asList(
                new EntityMetadataMessage(id, metadata.getEntryList()),
                new EntityTeleportMessage(id, x, y, z, yaw, pitch),
                new EntityVelocityMessage(id, getVelocity())
        );
    }

    @Override
    protected void pulsePhysics() {
        if (location.getY() < 0) {
            remove();
        } else if (!location.clone().add(getVelocity()).getBlock().getType().isSolid()) {
            location.add(getVelocity());
            if (location.getBlock().isLiquid()) {
                velocity.multiply(LIQUID_DRAG);
            } else {
                velocity.multiply(AIR_DRAG);
            }
            velocity.add(GRAVITY);
        } else {
            location.getBlock().setType(material);
            remove();
            world.playSound(location, Sound.BLOCK_ANVIL_FALL, 0.3f, (float) (1 + Math.random()));
        }

        super.pulsePhysics();
    }


    @Override
    public EntityType getType() {
        return EntityType.FALLING_BLOCK;
    }

}
