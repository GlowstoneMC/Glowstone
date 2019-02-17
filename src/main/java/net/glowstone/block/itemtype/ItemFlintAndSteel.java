package net.glowstone.block.itemtype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockTnt;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.pattern.PortalShape;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class ItemFlintAndSteel extends ItemTool {

    public ItemFlintAndSteel() {
        this.setPlaceAs(Material.FIRE);
    }

    @Override
    public boolean onToolRightClick(GlowPlayer player, GlowBlock target, BlockFace face,
                                    ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        if (target.getType() == Material.TNT) {
            fireTnt(target, player);
            return true;
        }
        if (tryFireNetherPortal(target, face)) {
            return true;
        }

        if (target.isFlammable() || target.getType().isOccluding()) {
            setBlockOnFire(player, target, face, holding, clickedLoc, hand);
            return true;
        }
        return false;
    }

    /**
     * Try to fire a nether portal at the given position.
     *
     * @param target the target block
     * @param face   the face from which the block was fired
     * @return whether a portal could be fired
     */
    private boolean tryFireNetherPortal(GlowBlock target, BlockFace face) {
        // Where fire would be placed if this is not a portal
        Location fireLocation =
                target.getLocation().add(face.getModX(), face.getModY(), face.getModZ());

        PortalShape shape = new PortalShape(fireLocation, BlockFace.WEST);
        if (!shape.validate() || shape.getPortalBlockCount() != 0) {
            shape = new PortalShape(fireLocation, BlockFace.NORTH);
            if (!shape.validate() || shape.getPortalBlockCount() != 0) {
                return false;
            }
            shape.placePortalBlocks();
            return true;
        }
        shape.placePortalBlocks();
        return true;
    }

    private void fireTnt(GlowBlock tnt, GlowPlayer player) {
        BlockTnt.igniteBlock(tnt, false, player);
    }

    private boolean setBlockOnFire(GlowPlayer player, GlowBlock clicked, BlockFace face,
                                   ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        GlowBlock fireBlock = clicked.getRelative(face);
        if (fireBlock.getType() != Material.AIR) {
            return true;
        }

        if (!clicked.isFlammable()
                && clicked.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            return true;
        }

        BlockIgniteEvent event = EventFactory.getInstance().callEvent(
                new BlockIgniteEvent(fireBlock, IgniteCause.FLINT_AND_STEEL, player, null));
        if (event.isCancelled()) {
            player.setItemInHand(holding);
            return false;
        }

        // clone holding to avoid decreasing of the item's amount
        ItemTable.instance().getBlock(Material.FIRE)
                .rightClickBlock(player, clicked, face, holding.clone(), clickedLoc, hand);

        return true;
    }
}
