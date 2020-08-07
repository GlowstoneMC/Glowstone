package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.constants.GameRules;
import net.glowstone.constants.GlowBiomeClimate;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public class BlockFire extends BlockNeedsAttached {

    private static final BlockFace[] RAIN_FACES = {BlockFace.SELF, BlockFace.NORTH, BlockFace.SOUTH,
        BlockFace.EAST, BlockFace.WEST};
    private static final int TICK_RATE = 20;
    private static final int MAX_FIRE_AGE = 15;
    private static final LinkedHashMap<BlockFace, Integer> BURNRESISTANCE_MAP
            = new LinkedHashMap<>();

    static {
        BURNRESISTANCE_MAP.put(BlockFace.EAST, 300);
        BURNRESISTANCE_MAP.put(BlockFace.WEST, 300);
        BURNRESISTANCE_MAP.put(BlockFace.DOWN, 250);
        BURNRESISTANCE_MAP.put(BlockFace.UP, 250);
        BURNRESISTANCE_MAP.put(BlockFace.NORTH, 300);
        BURNRESISTANCE_MAP.put(BlockFace.SOUTH, 300);
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return true;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        state.setRawData((byte) 0);
        state.getBlock().getWorld().requestPulse(state.getBlock());
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Collections.emptyList();
    }

    @Override
    protected BlockFace getAttachedFace(GlowBlock me) {
        for (BlockFace face : new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.EAST,
            BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH}) {
            if (!me.getRelative(face).isEmpty()) {
                return face;
            }
        }
        return BlockFace.DOWN;
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (!block.getWorld().getGameRuleMap().getBoolean(GameRules.DO_FIRE_TICK)) {
            return;
        }

        GlowWorld world = block.getWorld();
        Material type = block.getRelative(BlockFace.DOWN).getType();
        boolean isInfiniteFire = false;
        switch (type) {
            case NETHERRACK:
            case MAGMA_BLOCK:
                isInfiniteFire = true;
                break;
            case BEDROCK:
                if (world.getEnvironment() == Environment.THE_END) {
                    isInfiniteFire = true;
                }
                break;
            default:
                break;
        }
        if (!isInfiniteFire && world.hasStorm() && isRainingAround(block)) {
            // if it's raining around, stop fire
            block.breakNaturally();
            return;
        }

        // increase fire age
        GlowBlockState state = block.getState();
        int age = state.getRawData();
        if (age < MAX_FIRE_AGE) {
            // increase fire age
            state.setRawData((byte) (age + ThreadLocalRandom.current().nextInt(3) / 2));
            state.update(true);
        }

        if (isInfiniteFire) {
            return;
        }
        if (!hasNearFlammableBlock(block)) {
            // there's no flammable blocks around, stop fire
            if (age > 3 || block.getRelative(BlockFace.DOWN).isEmpty()) {
                block.breakNaturally();
                world.cancelPulse(block);
            }
            return;
        }
        if (age == MAX_FIRE_AGE && !block.getRelative(BlockFace.DOWN).isFlammable()
            && ThreadLocalRandom.current().nextInt(4) == 0) {
            // if fire reached max age, bottom block is not flammable, 25% chance to stop fire
            block.breakNaturally();
            world.cancelPulse(block);
            return;
        }
        // fire propagation / block burning

        // burn blocks around
        boolean isWet = GlowBiomeClimate.isWet(block);
        for (Entry<BlockFace, Integer> entry : BURNRESISTANCE_MAP.entrySet()) {
            burnBlock(block.getRelative(entry.getKey()), block,
                entry.getValue() - (isWet ? 50 : 0), age);
        }

        Difficulty difficulty = world.getDifficulty();
        int difficultyModifier;
        switch (difficulty) {
            case EASY:
                difficultyModifier = 7;
                break;
            case NORMAL:
                difficultyModifier = 14;
                break;
            case HARD:
                difficultyModifier = 21;
                break;
            default:
                difficultyModifier = 0;
                break;
        }

        // try to propagate fire in a 3x3x6 box
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 4; y++) {
                    if (x == 0 && z == 0 && y == 0) {
                        continue;
                    }
                    GlowBlock propagationBlock = world
                        .getBlockAt(block.getLocation().add(x, y, z));
                    int flameResistance = getFlameResistance(propagationBlock);
                    if (flameResistance <= 0) {
                        continue;
                    }
                    int resistance =
                        (40 + difficultyModifier + flameResistance) / (30 + age);
                    if (isWet) {
                        resistance /= 2;
                    }
                    if ((world.hasStorm() && isRainingAround(propagationBlock))
                            || resistance <= 0
                            || ThreadLocalRandom.current().nextInt(
                                    y > 1 ? 100 + 100 * (y - 1) : 100)
                            > resistance) {
                        continue;
                    }
                    BlockIgniteEvent igniteEvent = new BlockIgniteEvent(
                        propagationBlock, IgniteCause.SPREAD, block);
                    EventFactory.getInstance()
                            .callEvent(igniteEvent);
                    if (igniteEvent.isCancelled()) {
                        continue;
                    }
                    if (propagationBlock.getType() == Material.TNT) {
                        BlockTnt.igniteBlock(propagationBlock, false);
                    } else {
                        int increasedAge = increaseFireAge(age);
                        state = propagationBlock.getState();
                        state.setType(Material.FIRE);
                        state.setRawData((byte) (increasedAge > MAX_FIRE_AGE
                            ? MAX_FIRE_AGE : increasedAge));
                        state.update(true);
                        world.requestPulse(propagationBlock);
                    }
                }
            }
        }
    }

    @Override
    public void receivePulse(GlowBlock block) {
        updateBlock(block);
    }

    private boolean hasNearFlammableBlock(GlowBlock block) {
        // check there's at least a flammable block around
        for (BlockFace face : ADJACENT) {
            if (block.getRelative(face).isFlammable()) {
                return true;
            }
        }
        return false;
    }

    private int getFlameResistance(GlowBlock block) {
        if (!block.isEmpty()) {
            return 0;
        } else {
            int flameResistance = 0;
            for (BlockFace face : ADJACENT) {
                flameResistance = Math.max(flameResistance,
                    block.getRelative(face).getMaterialValues().getFlameResistance());
            }
            return flameResistance;
        }
    }

    private boolean isRainingAround(GlowBlock block) {
        // check if it's raining on the block itself or on one of it's 4 faces
        for (BlockFace face : RAIN_FACES) {
            if (GlowBiomeClimate.isRainy(block.getRelative(face))) {
                return true;
            }
        }
        return false;
    }

    private void burnBlock(GlowBlock block, GlowBlock from, int burnResistance, int fireAge) {
        if (ThreadLocalRandom.current().nextInt(burnResistance) < block.getMaterialValues()
            .getFireResistance()) {
            BlockBurnEvent burnEvent = new BlockBurnEvent(block, from);
            EventFactory.getInstance().callEvent(burnEvent);
            if (!burnEvent.isCancelled()) {
                if (block.getType() == Material.TNT) {
                    BlockTnt.igniteBlock(block, false);
                } else {
                    GlowBlockState state = block.getState();
                    if (ThreadLocalRandom.current().nextInt(10 + fireAge) < 5 && !GlowBiomeClimate
                        .isRainy(block)) {
                        int increasedAge = increaseFireAge(fireAge);
                        state.setType(Material.FIRE);
                        state.setRawData(
                            (byte) (increasedAge > MAX_FIRE_AGE ? MAX_FIRE_AGE : increasedAge));
                    } else {
                        state.setType(Material.AIR);
                        state.setRawData((byte) 0);
                    }
                    state.update(true);
                }
            }
        }
    }

    private int increaseFireAge(int age) {
        return age + ThreadLocalRandom.current().nextInt(5) / 4;
    }

    @Override
    public int getPulseTickSpeed(GlowBlock block) {
        return TICK_RATE;
    }

    @Override
    public boolean isPulseOnce(GlowBlock block) {
        return false;
    }
}
