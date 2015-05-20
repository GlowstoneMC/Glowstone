package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockWoodenTrapDoor extends BlockOpenable {
    private static final Collection<ItemStack> DROP = Collections.unmodifiableCollection(Arrays.asList(new ItemStack(Material.TRAP_DOOR)));
    private BlockTrapDoor trapDoor;

    public BlockWoodenTrapDoor() {
        trapDoor = new BlockTrapDoor(this);
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        trapDoor.placeBlock(player, state, face, holding, clickedLoc);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return DROP;
    }

    @Override
    public void updatePhysics(GlowBlock me) {
        trapDoor.updatePhysics(me);
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState) {
        trapDoor.updatePhysics(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        trapDoor.onNearBlockChanged(block, face, changedBlock, oldType, oldData, newType, newData);
    }
}
