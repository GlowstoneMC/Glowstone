package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
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
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        final GlowBlockState state = block.getState();
        final MaterialData data = state.getData();

        if (!(data instanceof Button)) {
            warnMaterialData(Button.class, data);
            return false;
        }

        final Button button = (Button) data;

        if (button.isPowered()) {
            return true;
        }

        button.setPowered(true);
        state.update();

        // todo: switch to block scheduling system when one is available
        (new BukkitRunnable() {
            @Override
            public void run() {
                button.setPowered(false);
                state.update();
            }
        }).runTaskLater(null, 20);

        return true;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();

        if (!(data instanceof Button)) {
            warnMaterialData(Button.class, data);
            return;
        }

        setAttachedFace(state, face.getOppositeFace());
    }
}
