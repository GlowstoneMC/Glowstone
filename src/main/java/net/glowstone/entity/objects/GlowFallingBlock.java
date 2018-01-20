package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.entity.GlowEntity;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GlowFallingBlock extends GlowEntity implements FallingBlock {

    @Getter
    @Setter
    private Material material;
    private boolean canHurtEntities;
    @Setter
    private boolean dropItem;
    @Getter
    @Setter
    private byte blockData;
    private Location sourceLocation;
    @Getter
    @Setter
    private CompoundTag blockEntityCompoundTag;

    // todo: implement falling block damage
    /*
    private boolean fallHurtMax;
    private boolean fallHurtAmount;
    also FallingBlockStore values
     */
    // todo: implement slow in cobwebs (might just be global entity thing)
    // todo: implement anvils sometimes taking damage

    public GlowFallingBlock(Location location, Material material, byte blockData) {
        this(location, material, blockData, null);
    }

    /**
     * Creates an instance for the given entity.
     *
     * @param location the falling block's location
     * @param material the falling block's material
     * @param blockData the falling block's data value
     * @param blockEntity the entity
     */
    public GlowFallingBlock(Location location, Material material, byte blockData,
        BlockEntity blockEntity) {
        super(location);
        blockEntityCompoundTag = null;
        if (blockEntity != null) {
            blockEntityCompoundTag = new CompoundTag();
            blockEntity.saveNbt(blockEntityCompoundTag);
        }
        this.sourceLocation = location.clone();
        setBoundingBox(0.98, 0.98);
        setDrag(0.98, false);
        setGravityAccel(new Vector(0, -0.02, 0));

        setMaterial(material);
        setDropItem(true);
        setHurtEntities(true);
        this.blockData = blockData;
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
    public int getBlockId() {
        return material.getId();
    }

    @Override
    public boolean getDropItem() {
        return dropItem;
    }

    @Override
    public List<Message> createSpawnMessage() {

        // Note the shift amount has changed previously,
        // if block data doesn't appear to work check this value.
        int blockIdData = getBlockId() | getBlockData() << 12;

        return Collections.singletonList(
            new SpawnObjectMessage(entityId, getUniqueId(), 70, location, blockIdData)
        );
    }

    @Override
    protected void pulsePhysics() {
        if (location.getY() < 0) {
            remove();
            return;
        }

        Location nextBlock = location.clone().add(getVelocity());
        if (!nextBlock.getBlock().getType().isSolid()) {
            velocity.add(getGravityAccel());
            location.add(getVelocity());
            velocity.multiply(airDrag);
        } else {
            if (supportingBlock(location.getBlock().getType())) {
                boolean replaceBlock;
                switch (location.getBlock().getType()) {
                    case DEAD_BUSH:
                    case LONG_GRASS:
                    case DOUBLE_PLANT:
                        replaceBlock = true;
                        break;
                    default:
                        replaceBlock = false;
                        break;
                }
                if (replaceBlock) {
                    setDropItem(false);
                }
                // todo: add event if desired
                if (getDropItem()) {
                    world.dropItemNaturally(location,
                        new ItemStack(material, 1, (short) 0, getBlockData()));
                }
                if (replaceBlock) {
                    placeFallingBlock();
                }
                remove();
            } else {
                placeFallingBlock();
                if (material == Material.ANVIL) {
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    world.playSound(location, Sound.BLOCK_ANVIL_FALL, 4, (1.0F
                            + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F);
                }
                remove();
            }
        }

        updateBoundingBox();
    }

    private void placeFallingBlock() {
        location.getBlock().setTypeIdAndData(material.getId(), getBlockData(), true);
        if (getBlockEntityCompoundTag() != null) {
            if (location.getBlock() instanceof GlowBlock) {
                GlowBlock block = (GlowBlock) location.getBlock();
                BlockEntity blockEntity = block.getBlockEntity();
                if (blockEntity != null) {
                    blockEntity.loadNbt(getBlockEntityCompoundTag());
                }
            }
        }
    }


    @Override
    public EntityType getType() {
        return EntityType.FALLING_BLOCK;
    }

    private boolean supportingBlock(Material material) {
        switch (material) {
            case AIR:
            case FIRE:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
                return false;
            default:
                return true;
        }
    }

}
