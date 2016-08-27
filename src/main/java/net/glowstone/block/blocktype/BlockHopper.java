package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEContainer;
import net.glowstone.block.entity.TEHopper;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Hopper;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class BlockHopper extends BlockContainer {

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
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TEHopper(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
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
        if (block.getTileEntity() == null) {
            return;
        }
        TEHopper hopper = (TEHopper) block.getTileEntity();
        pullItems(block, hopper);
        if (!((Hopper) block.getState().getData()).isPowered()) {
            pushItems(block, hopper);
        }
    }

    @Override
    public void onRedstoneUpdate(GlowBlock block) {
        ((Hopper) block.getState().getData()).setActive(!block.isBlockPowered());
    }

    private void pullItems(GlowBlock block, TEHopper hopper) {
        GlowBlock source = block.getRelative(BlockFace.UP);
        if (source.getType() == null || source.getType() == Material.AIR) {
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
        } else if (source.getTileEntity() != null && source.getTileEntity() instanceof TEContainer) {
            TEContainer sourceContainer = (TEContainer) source.getTileEntity();
            if (sourceContainer.getInventory() == null || sourceContainer.getInventory().getContents().length == 0) {
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

    private void pushItems(GlowBlock block, TEHopper hopper) {
        if (hopper.getInventory() == null || hopper.getInventory().getContents().length == 0) {
            return;
        }
        GlowBlock target = block.getRelative(((Hopper) block.getState().getData()).getFacing());
        if (target.getType() != null && target.getTileEntity() instanceof TEContainer) {
            ItemStack item = getFirstItem(hopper);
            if (item == null) {
                return;
            }
            ItemStack clone = item.clone();
            clone.setAmount(1);
            if (((TEContainer) target.getTileEntity()).getInventory().addItem(clone).size() > 0) {
                return;
            }

            if (item.getAmount() - 1 == 0) {
                hopper.getInventory().remove(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        }
    }

    private GlowItem getFirstDroppedItem(Location location) {
        for (Entity entity : location.getChunk().getEntities()) {
            if (location.getBlockX() != entity.getLocation().getBlockX() || location.getBlockY() != entity.getLocation().getBlockY() || location.getBlockZ() != entity.getLocation().getBlockZ()) {
                continue;
            }
            if (entity.getType() != EntityType.DROPPED_ITEM) {
                continue;
            }
            return ((GlowItem) entity);
        }
        return null;
    }

    private ItemStack getFirstItem(TEContainer container) {
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

    @Override
    public boolean canTickRandomly() {
        return true;
    }
}
