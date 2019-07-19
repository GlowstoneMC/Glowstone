package net.glowstone.block.entity.state;

import com.destroystokyo.paper.loottable.LootableBlockInventory;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import org.bukkit.Nameable;
import org.bukkit.block.Container;
import org.bukkit.block.Lockable;
import org.bukkit.inventory.Inventory;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public abstract class GlowContainer extends GlowBlockState implements LootableBlockInventory,
    Lockable, Nameable, Container {
    private final AtomicLong lastFilled = new AtomicLong(-1);
    private final AtomicLong nextRefill = new AtomicLong(-1);
    private final AtomicLong lootTableSeed = new AtomicLong(0);
    private final AtomicReference<LootTable> lootTable = new AtomicReference<>(null);
    private final Map<UUID, Long> playersWhoHaveLooted = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private String lock;
    @Getter
    @Setter
    private String customName;

    public GlowContainer(GlowBlock block) {
        super(block);
    }

    @Override
    public void setLootTable(@Nullable LootTable table) {
        lootTable.set(table);
    }

    @Override
    public void setLootTable(@Nullable LootTable table, long seed) {
        setSeed(seed);
        setLootTable(table);
    }

    @Override
    public @Nullable LootTable getLootTable() {
        return lootTable.get();
    }

    @Override
    public boolean hasLootTable() {
        return lootTable.get() != null;
    }

    public long setLootTableSeed(long seed) {
        return lootTableSeed.getAndSet(seed);
    }

    @Override
    public void clearLootTable() {
        setLootTable(null);
    }

    @Override
    public void setSeed(long seed) {
        lootTableSeed.set(seed);
    }

    @Override
    public long getSeed() {
        return lootTableSeed.get();
    }

    @Override
    public boolean isRefillEnabled() {
        // TODO
        return false;
    }

    @Override
    public boolean hasBeenFilled() {
        return lastFilled.get() >= 0;
    }

    @Override
    public boolean hasPlayerLooted(UUID uuid) {
        return playersWhoHaveLooted.containsKey(uuid);
    }

    @Override
    public Long getLastLooted(UUID uuid) {
        return playersWhoHaveLooted.get(uuid);
    }

    @Override
    public boolean setHasPlayerLooted(UUID uuid, boolean b) {
        return b
                ? playersWhoHaveLooted.put(uuid, getWorld().getFullTime()) != null
                : playersWhoHaveLooted.remove(uuid) != null;
    }

    @Override
    public boolean hasPendingRefill() {
        return getNextRefill() >= Math.max(0, getLastFilled());
    }

    @Override
    public long getLastFilled() {
        return lastFilled.get();
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
        return getLock() != null;
    }

    @Override
    public Inventory getSnapshotInventory() {
        // TODO
        throw new UnsupportedOperationException();
    }
}
