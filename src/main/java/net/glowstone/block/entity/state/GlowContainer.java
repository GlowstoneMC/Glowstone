package net.glowstone.block.entity.state;

import com.destroystokyo.paper.loottable.LootableBlockInventory;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import org.bukkit.Nameable;
import org.bukkit.block.Container;
import org.bukkit.block.Lockable;
import org.bukkit.inventory.Inventory;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class GlowContainer extends GlowBlockState implements LootableBlockInventory,
    Lockable, Nameable, Container {
    private final AtomicLong nextRefill = new AtomicLong(-1);
    private final AtomicLong lootTableSeed = new AtomicLong(0);
    private String lock;
    private String customName;

    public GlowContainer(GlowBlock block) {
        super(block);
    }

    // TODO: Lootable implementation

    @Override
    public String getLootTableName() {
        return null;
    }

    @Override
    public boolean hasLootTable() {
        return false;
    }

    @Override
    public String setLootTable(String name) {
        return null;
    }

    @Override
    public String setLootTable(String s, long l) {
        return null;
    }

    @Override
    public long getLootTableSeed() {
        return lootTableSeed.get();
    }

    @Override
    public long setLootTableSeed(long seed) {
        return lootTableSeed.getAndSet(seed);
    }

    @Override
    public void clearLootTable() {

    }

    @Override
    public boolean isRefillEnabled() {
        return false;
    }

    @Override
    public boolean hasBeenFilled() {
        return false;
    }

    @Override
    public boolean hasPlayerLooted(UUID uuid) {
        return false;
    }

    @Override
    public Long getLastLooted(UUID uuid) {
        return null;
    }

    @Override
    public boolean setHasPlayerLooted(UUID uuid, boolean b) {
        return false;
    }

    @Override
    public boolean hasPendingRefill() {
        return getNextRefill() >= 0;
    }

    @Override
    public long getLastFilled() {
        return 0;
    }

    @Override
    public long getNextRefill() {
        return nextRefill.get();
    }

    @Override
    public long setNextRefill(long l) {
        return nextRefill.getAndSet(l);
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public Inventory getSnapshotInventory() {
        throw new UnsupportedOperationException();
    }
}
