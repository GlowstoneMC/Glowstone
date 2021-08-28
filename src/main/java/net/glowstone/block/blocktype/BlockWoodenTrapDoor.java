package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockWoodenTrapDoor extends BlockOpenable {

    // TODO: 1.13: new trap door types
    private static final Collection<ItemStack> DROP =
        Collections.unmodifiableCollection(Arrays.asList(new ItemStack(Material.LEGACY_TRAP_DOOR)));
    private BlockTrapDoor trapDoor;

    public BlockWoodenTrapDoor() {
        trapDoor = new BlockTrapDoor(this);
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
                           ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        trapDoor.placeBlock(player, state, face, holding, clickedLoc);
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return DROP;
    }

    @Override
    public void onRedstoneUpdate(GlowBlock block) {
        trapDoor.onRedstoneUpdate(block);
    }
}
