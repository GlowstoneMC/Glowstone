package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class BlockFire extends BlockNeedsAttached {

    private static final BlockFace[] FLAMMABLE_FACES = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
    private static final BlockFace[] RAIN_FACES = {BlockFace.SELF, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
    private static final int TICK_RATE = 1;
    private static final int MAX_FIRE_AGE = 15;
    private static final LinkedHashMap<BlockFace, Integer> BURNRESISTANCE_MAP = new LinkedHashMap<>();

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return true;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        state.setRawData((byte) 0);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return BlockDropless.EMPTY_STACK;
    }

    @Override
    protected BlockFace getAttachedFace(GlowBlock me) {
        for (BlockFace face : new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH}) {
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
        if (!block.getWorld().getGameRuleMap().getBoolean("doFireTick")) {
            return;
        }

        final GlowWorld world = block.getWorld();
        final Material type = block.getRelative(BlockFace.DOWN).getType();
        boolean isInfiniteFire = type == Material.NETHERRACK || (world.getEnvironment() == Environment.THE_END && type == Material.BEDROCK);
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
            state.setRawData((byte) (age + (random.nextInt(3) / 2)));
            state.update(true);
        }

        // request pulse for this block
        world.requestPulse(block, TICK_RATE);

        if (!isInfiniteFire) {
            if (!hasNearFlammableBlock(block)) {
                // there's no flammable blocks around, stop fire
                if (age > 3 || block.getRelative(BlockFace.DOWN).isEmpty()) {
                    block.breakNaturally();
                }
            } else if (age == MAX_FIRE_AGE && !block.getRelative(BlockFace.DOWN).isFlammable() && random.nextInt(4) == 0) {
                // if fire reached max age, bottom block is not flammable, 25% chance to stop fire
                block.breakNaturally();
            } else {
                // fire propagation / block burning

                // burn blocks around
                boolean isWet = GlowBiomeClimate.isWet(block);
                for (Entry<BlockFace, Integer> entry : BURNRESISTANCE_MAP.entrySet()) {
                    burnBlock(block.getRelative(entry.getKey()), entry.getValue() - (isWet ? 50 : 0), age);
                }

                final Difficulty difficulty = world.getDifficulty();
                final int difficultyModifier = difficulty == Difficulty.EASY ? 7 : difficulty == Difficulty.NORMAL ? 14 : difficulty == Difficulty.HARD ? 21 : 0;

                // try to propagate fire in a 3x3x6 box
                for (int x = 0; x < 3; x++) {
                    for (int z = 0; z < 3; z++) {
                        for (int y = 0; y < 6; y++) {
                            if (x != 1 || z != 1 || y != 1) {
                                final GlowBlock propagationBlock = world.getBlockAt(block.getLocation().add(x - 1, y - 1, z - 1));
                                int flameResistance = propagationBlock.getMaterialValues().getFlameResistance();
                                if (flameResistance >= 0) {
                                    int resistance = 40 + difficultyModifier + flameResistance;
                                    resistance /= 30 + age;
                                    if (isWet) {
                                        resistance /= 2;
                                    }
                                    if ((!world.hasStorm() || !isRainingAround(propagationBlock))
                                            && resistance > 0 && random.nextInt(y > 2 ? 100 + 100 * (y - 2) : 100) <= resistance) {
                                        BlockIgniteEvent igniteEvent = new BlockIgniteEvent(propagationBlock, IgniteCause.SPREAD, block);
                                        EventFactory.callEvent(igniteEvent);
                                        if (!igniteEvent.isCancelled()) {
                                            if (propagationBlock.getType() == Material.TNT) {
                                                BlockTNT.igniteBlock(propagationBlock, false);
                                            } else {
                                                int increasedAge = increaseFireAge(age);
                                                state = propagationBlock.getState();
                                                state.setType(Material.FIRE);
                                                state.setRawData((byte) (increasedAge > MAX_FIRE_AGE ? MAX_FIRE_AGE : increasedAge));
                                                state.update(true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void receivePulse(GlowBlock block) {
        block.getWorld().cancelPulse(block);
        updateBlock(block);
    }

    private boolean hasNearFlammableBlock(GlowBlock block) {
        // check there's at least a flammable block around
        for (BlockFace face : FLAMMABLE_FACES) {
            if (block.getRelative(face).isFlammable()) {
                return true;
            }
        }
        return false;
    }

    private boolean isRainingAround(GlowBlock block) {
        // check if it's raining on the block itself or on one of it's 4 faces
        for (BlockFace face: RAIN_FACES) {
            if (GlowBiomeClimate.isRainy(block.getRelative(face))) {
                return true;
            }
        }
        return false;
    }

    private void burnBlock(GlowBlock block, int burnResistance, int fireAge) {
        if (random.nextInt(burnResistance) < block.getMaterialValues().getFireResistance()) {
            BlockBurnEvent burnEvent = new BlockBurnEvent(block);
            EventFactory.callEvent(burnEvent);
            if (!burnEvent.isCancelled()) {
                if (block.getType() == Material.TNT) {
                    BlockTNT.igniteBlock(block, false);
                } else {
                    final GlowBlockState state = block.getState();
                    if (random.nextInt(10 + fireAge) < 5 && !GlowBiomeClimate.isRainy(block)) {
                        int increasedAge = increaseFireAge(fireAge);
                        state.setType(Material.FIRE);
                        state.setRawData((byte) (increasedAge > MAX_FIRE_AGE ? MAX_FIRE_AGE : increasedAge));
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
        return age + random.nextInt(5) / 4;
    }

    static {
        BURNRESISTANCE_MAP.put(BlockFace.EAST, 300);
        BURNRESISTANCE_MAP.put(BlockFace.WEST, 300);
        BURNRESISTANCE_MAP.put(BlockFace.DOWN, 250);
        BURNRESISTANCE_MAP.put(BlockFace.UP, 250);
        BURNRESISTANCE_MAP.put(BlockFace.NORTH, 300);
        BURNRESISTANCE_MAP.put(BlockFace.SOUTH, 300);
    }
}
