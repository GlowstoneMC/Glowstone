package net.glowstone.scheduler;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class PulseTask extends BukkitRunnable {

    private final Location location;
    private final Material originalMaterial;
    private boolean async;
    private long delay;
    private boolean single;

    public PulseTask(GlowBlock block, boolean async, long delay, boolean single) {
        this.location = block.getLocation();
        this.originalMaterial = block.getType();
        this.async = async;
        this.delay = delay;
        this.single = single;
    }

    public void startPulseTask() {
        if (single) {
            if (async) {
                runTaskLaterAsynchronously(null, delay);
            } else {
                runTaskLater(null, delay);
            }
        } else {
            if (async) {
                runTaskTimerAsynchronously(null, delay, delay);
            } else {
                runTaskTimer(null, delay, delay);
            }
        }
    }

    @Override
    public void run() {
        Block block = location.getBlock();
        if (block == null) {
            cancel();
            return;
        }
        if (!location.getChunk().isLoaded()) {
            return;
        }
        if (block.getType() != originalMaterial) {
            cancel();
            return;
        }
        ItemTable table = ItemTable.instance();
        BlockType type = table.getBlock(originalMaterial);
        if (type != null) {
            type.receivePulse((GlowBlock) block);
        }
    }
}
