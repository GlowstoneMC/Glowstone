package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BlockButton extends BlockAttachable {

    public BlockButton(Material material) {
        setDrops(new ItemStack(material));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
                                 Vector clickedLoc) {
        GlowBlockState state = block.getState();
        MaterialData data = state.getData();

        if (!(data instanceof Button)) {
            warnMaterialData(Button.class, data);
            return false;
        }

        Button button = (Button) data;

        if (button.isPowered()) {
            return true;
        }

        button.setPowered(true);
        state.update();
        extraUpdate(block);

        // todo: switch to block scheduling system when one is available
        new BukkitRunnable() {
            @Override
            public void run() {
                button.setPowered(false);
                state.update();
                // TODO: 1.13 wood button types
                if (block.getType() == Material.LEGACY_WOOD_BUTTON
                    || block.getType() == Material.STONE_BUTTON) {
                    extraUpdate(block);
                    block.getWorld().playSound(block.getLocation(),
                        block.getType() == Material.LEGACY_WOOD_BUTTON
                            ? Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF
                            : Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 0.3f, 0.5f);
                }
            }
        }.runTaskLater(null, block.getType() == Material.STONE_BUTTON ? 20 : 30);

        return true;
    }

    private void extraUpdate(GlowBlock block) {
        Button button = (Button) block.getState().getData();
        ItemTable itemTable = ItemTable.instance();
        GlowBlock target = block.getRelative(button.getAttachedFace());
        if (target.getType().isSolid()) {
            for (BlockFace face2 : ADJACENT) {
                GlowBlock target2 = target.getRelative(face2);
                BlockType notifyType = itemTable.getBlock(target2.getType());
                if (notifyType != null) {
                    if (target2.getFace(block) == null) {
                        notifyType
                            .onNearBlockChanged(target2, BlockFace.SELF, block, block.getType(),
                                block.getData(), block.getType(), block.getData());
                    }
                    notifyType.onRedstoneUpdate(target2);
                }
            }
        }
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
                           ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();

        if (!(data instanceof Button)) {
            warnMaterialData(Button.class, data);
            return;
        }

        setAttachedFace(state, face.getOppositeFace());
    }
}
