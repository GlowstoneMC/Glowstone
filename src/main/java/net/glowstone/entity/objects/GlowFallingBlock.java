package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.entity.GlowEntity;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GlowFallingBlock extends GlowEntity implements FallingBlock {
    private static final double VERTICAL_GRAVITY_ACCEL = -0.04;

    @Getter
    @Setter
    private BlockData blockData;
    private boolean canHurtEntities;
    @Setter
    private boolean dropItem;
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

    public GlowFallingBlock(Location location, BlockData data) {
        this(location, data, null);
    }

    /**
     * Creates an instance for the given entity.
     *
     * @param location    the falling block's location
     * @param blockData   the falling block's BlockData
     * @param blockEntity the entity
     */
    public GlowFallingBlock(Location location, BlockData blockData,
                            BlockEntity blockEntity) {
        super(location);
        blockEntityCompoundTag = null;
        if (blockEntity != null) {
            blockEntityCompoundTag = new CompoundTag();
            blockEntity.saveNbt(blockEntityCompoundTag);
        }
        this.sourceLocation = location.clone();
        setBoundingBox(0.98, 0.98);
        setAirDrag(0.98);
        setGravityAccel(new Vector(0, VERTICAL_GRAVITY_ACCEL, 0));
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
    public boolean doesAutoExpire() {
        return true;
    }

    @Override
    public void shouldAutoExpire(boolean autoExpires) {

    }

    @Override
    public Material getMaterial() {
        return getBlockData().getMaterial();
    }

    @Override
    public boolean getDropItem() {
        return dropItem;
    }

    @Override
    public List<Message> createSpawnMessage() {
        // TODO: 1.13: Flatten BlockData to integer
        // return Collections.singletonList(
        //    new SpawnObjectMessage(entityId, getUniqueId(), 70, location, blockIdData)
        // );
        return Collections.emptyList();
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
                    case TALL_GRASS: // TODO: 1.13, DOUBLE_PLANT
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
                    // TODO: 1.13, convert BlockData to MaterialData
                    world.dropItemNaturally(location,
                        new ItemStack(getMaterial()));
                }
                if (replaceBlock) {
                    placeFallingBlock();
                }
                remove();
            } else {
                placeFallingBlock();
                remove();
            }
        }

        updateBoundingBox();
    }

    private void placeFallingBlock() {
        location.getBlock().setBlockData(getBlockData(), true);
        if (getBlockEntityCompoundTag() != null) {
            if (location.getBlock() instanceof GlowBlock) {
                GlowBlock block = (GlowBlock) location.getBlock();
                BlockEntity blockEntity = block.getBlockEntity();
                if (blockEntity != null) {
                    blockEntity.loadNbt(getBlockEntityCompoundTag());
                }
            }
        }
        // TODO: damaged anvils too
        if (getBlockData().getMaterial() == Material.ANVIL) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            world.playSound(location, Sound.BLOCK_ANVIL_FALL, 4, (1.0F
                + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F);
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
            case LAVA:
                return false;
            default:
                return true;
        }
    }
}
