package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BeaconEntity;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Beacon;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockBeacon extends BlockDirectDrops {

    public BlockBeacon() {
        super(Material.BEACON);
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new BeaconEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        state.getBlock().getWorld().requestPulse(state.getBlock());
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        Beacon beacon = (Beacon) block.getState();
        player.openInventory(beacon.getInventory());
        player.incrementStatistic(Statistic.BEACON_INTERACTION);
        return true;
    }

    @Override
    public boolean isPulseOnce(GlowBlock block) {
        return false;
    }

    @Override
    public int getPulseTickSpeed(GlowBlock block) {
        return 80;
    }

    @Override
    public void receivePulse(GlowBlock block) {
        ((BeaconEntity) block.getBlockEntity()).onPulse();
    }
}
