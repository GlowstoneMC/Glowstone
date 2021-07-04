package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.ContainerEntity;
import net.glowstone.block.entity.HopperEntity;
import net.glowstone.block.entity.state.GlowHopper;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Hopper;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Rails;
import org.bukkit.material.Sign;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class BlockHopper extends BlockContainer {

    /**
     * Sets a hopper to face a given direction.
     * @param bs the hopper's BlockState
     * @param face the direction to face
     */
    public void setFacingDirection(BlockState bs, BlockFace face) {
        byte data;
        switch (face) {
            case DOWN:
                data = 0;
                break;
            case UP:
                data = 1;
                break;
            case NORTH:
                data = 2;
                break;
            case SOUTH:
                data = 3;
                break;
            case WEST:
                data = 4;
                break;
            case EAST:
            default:
                data = 5;
                break;
        }
        bs.setRawData(data);
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new HopperEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        setFacingDirection(state, face.getOppositeFace());
        state.getBlock().getWorld().requestPulse(state.getBlock());
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }

    @Override
    public void receivePulse(GlowBlock block) {
        if (block.getBlockEntity() == null) {
            return;
        }
        HopperEntity hopper = (HopperEntity) block.getBlockEntity();
        if (!((Hopper) block.getState().getData()).isPowered()) {
            pushItems(block, hopper);
        }
        pullItems(block, hopper);
    }

    @Override
    public void onRedstoneUpdate(GlowBlock block) {
        ((Hopper) block.getState().getData()).setActive(!block.isBlockPowered());
    }

    private void pullItems(GlowBlock block, HopperEntity hopper) {
        GlowBlock source = block.getRelative(BlockFace.UP);
        MaterialData data = source.getState().getData();
        if (!source.getType().isSolid()
                || (data instanceof Step && !((Step) data).isInverted())
                || (data instanceof WoodenStep && !((WoodenStep) data).isInverted())
                || (data instanceof Sign)
                || (data instanceof Rails)) {
            GlowItem item = getFirstDroppedItem(source.getLocation());
            if (item == null) {
                return;
            }
            ItemStack stack = item.getItemStack();
            HashMap<Integer, ItemStack> add = hopper.getInventory().addItem(stack);
            if (add.size() > 0) {
                item.setItemStack(add.get(0));
            } else {
                item.remove();
            }
        } else if (source.getBlockEntity() != null && source
            .getBlockEntity() instanceof ContainerEntity) {
            ContainerEntity sourceContainer = (ContainerEntity) source.getBlockEntity();
            if (sourceContainer.getInventory() == null
                || sourceContainer.getInventory().getContents().length == 0) {
                return;
            }
            ItemStack item = getFirstItem(sourceContainer);
            if (item == null) {
                return;
            }
            ItemStack clone = item.clone();
            clone.setAmount(1);
            if (hopper.getInventory().addItem(clone).size() > 0) {
                return;
            }
            if (item.getAmount() - 1 == 0) {
                sourceContainer.getInventory().remove(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        }
    }

    private boolean pushItems(GlowBlock block, HopperEntity hopper) {
        if (hopper.getInventory() == null || hopper.getInventory().getContents().length == 0) {
            return false;
        }
        GlowBlock target = block.getRelative(((Hopper) block.getState().getData()).getFacing());
        if (target.getType() != null && target.getBlockEntity() instanceof ContainerEntity) {
            if (target.getState() instanceof GlowHopper) {
                if (((Hopper) block.getState().getData()).getFacing() == BlockFace.DOWN) {
                    // If the hopper is facing downwards, the target hopper can do the pulling task
                    // itself
                    return false;
                }
            }
            ItemStack item = getFirstItem(hopper);
            if (item == null) {
                return false;
            }
            ItemStack clone = item.clone();
            clone.setAmount(1);
            if (((ContainerEntity) target.getBlockEntity()).getInventory().addItem(clone).size()
                > 0) {
                return false;
            }

            if (item.getAmount() - 1 == 0) {
                hopper.getInventory().remove(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
            return true;
        }
        return false;
    }

    private GlowItem getFirstDroppedItem(Location location) {
        for (Entity entity : location.getChunk().getEntities()) {
            if (location.getBlockX() != entity.getLocation().getBlockX()
                || location.getBlockY() != entity.getLocation().getBlockY()
                || location.getBlockZ() != entity.getLocation().getBlockZ()) {
                continue;
            }
            if (entity.getType() != EntityType.DROPPED_ITEM) {
                continue;
            }
            return ((GlowItem) entity);
        }
        return null;
    }

    private ItemStack getFirstItem(ContainerEntity container) {
        Inventory inventory = container.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == null) {
                continue;
            }
            return inventory.getItem(i);
        }
        return null;
    }

    @Override
    public boolean isPulseOnce(GlowBlock block) {
        return false;
    }

    @Override
    public int getPulseTickSpeed(GlowBlock block) {
        return 8;
    }
}
