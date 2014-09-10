package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockLever extends BlockType {

    public BlockLever() {
        setDrops(new ItemStack(Material.LEVER));
    }

    public void setAttachedFace(final Lever lever, final BlockFace attachedFace) {
        byte data = lever.getData();
        switch (attachedFace) {
            case WEST:
                data |= 1;
                break;
            case EAST:
                data |= 2;
                break;
            case NORTH:
                data |= 3;
                break;
            case SOUTH:
                data |= 4;
                break;
            case DOWN:
                data |= 5; // or 6
                break;
            case UP:
                data |= 7; // or 0
                break;
        }
        lever.setData(data);
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        final GlowBlockState state = block.getState();
        final MaterialData data = state.getData();
        if (data instanceof Lever) {
            final Lever l = (Lever) data;
            l.setPowered(!l.isPowered());
            state.setData(l);
            state.update();
            return true;
        } else {
            warnMaterialData(Lever.class, data);
            return false;
        }
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        final MaterialData data = state.getData();
        if (data instanceof Lever) {
            final Lever l = (Lever) data;
            setAttachedFace(l, face.getOppositeFace());
            l.setFacingDirection(face == BlockFace.UP || face == BlockFace.DOWN ? player.getDirection() : face);
            state.setData(l);
        } else {
            warnMaterialData(Lever.class, data);
        }
    }
}
