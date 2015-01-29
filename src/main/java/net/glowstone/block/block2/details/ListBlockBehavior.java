package net.glowstone.block.block2.details;

import io.netty.util.Signal;
import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.block2.BlockBehavior;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * BlockBehavior which uses the first child implementing each method.
 */
public final class ListBlockBehavior implements BlockBehavior {

    private static final Signal NEXT = BaseBlockBehavior.NEXT;
    private static final BlockBehavior fallback = DefaultBlockBehavior.instance;

    private final List<BlockBehavior> children;

    public ListBlockBehavior(List<BlockBehavior> children) {
        this.children = new ArrayList<>(children);
    }

    @Override
    public String toString() {
        return children.toString();
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        for (BlockBehavior child : children) {
            try {
                return child.getDrops(block, tool);
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        return fallback.getDrops(block, tool);
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        for (BlockBehavior child : children) {
            try {
                return child.createTileEntity(chunk, cx, cy, cz);
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        return fallback.createTileEntity(chunk, cx, cy, cz);
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        for (BlockBehavior child : children) {
            try {
                return child.canPlaceAt(block, against);
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        return fallback.canPlaceAt(block, against);
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        for (BlockBehavior child : children) {
            try {
                child.placeBlock(player, state, face, holding, clickedLoc);
                return;
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        fallback.placeBlock(player, state, face, holding, clickedLoc);
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
        for (BlockBehavior child : children) {
            try {
                child.afterPlace(player, block, holding);
                return;
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        fallback.afterPlace(player, block, holding);
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        for (BlockBehavior child : children) {
            try {
                return child.blockInteract(player, block, face, clickedLoc);
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        return fallback.blockInteract(player, block, face, clickedLoc);
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        for (BlockBehavior child : children) {
            try {
                child.blockDestroy(player, block, face);
                return;
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        fallback.blockDestroy(player, block, face);
    }

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        for (BlockBehavior child : children) {
            try {
                return child.canAbsorb(block, face, holding);
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        return fallback.canAbsorb(block, face, holding);
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        for (BlockBehavior child : children) {
            try {
                return child.canOverride(block, face, holding);
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        return fallback.canOverride(block, face, holding);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        for (BlockBehavior child : children) {
            try {
                child.onNearBlockChanged(block, face, changedBlock, oldType, oldData, newType, newData);
                return;
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        fallback.onNearBlockChanged(block, face, changedBlock, oldType, oldData, newType, newData);
    }

    @Override
    public void onBlockChanged(GlowBlock block, Material oldType, byte oldData, Material newType, byte data) {
        for (BlockBehavior child : children) {
            try {
                child.onBlockChanged(block, oldType, oldData, newType, data);
                return;
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        fallback.onBlockChanged(block, oldType, oldData, newType, data);
    }

    @Override
    public void updatePhysics(GlowBlock me) {
        for (BlockBehavior child : children) {
            try {
                child.updatePhysics(me);
                return;
            } catch (Signal signal) {
                signal.expect(NEXT);
            }
        }
        fallback.updatePhysics(me);
    }
}
