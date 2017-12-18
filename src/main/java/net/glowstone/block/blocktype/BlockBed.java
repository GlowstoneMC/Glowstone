package net.glowstone.block.blocktype;

import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BedEntity;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.state.GlowBed;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockBed extends BlockType {

    public BlockBed() {
        setDrops(new ItemStack(Material.BED));
    }

    /**
     * Helper method for set whether the specified bed blocks are occupied.
     *
     * @param head head of the bed
     * @param foot foot of the bed
     * @param occupied if the bed is occupied by a player
     */
    public static void setOccupied(GlowBlock head, GlowBlock foot, boolean occupied) {
        byte headData = head.getData();
        byte footData = foot.getData();
        head.setData((byte) (occupied ? headData | 0x4 : headData & ~0x4));
        foot.setData((byte) (occupied ? footData | 0x4 : footData & ~0x4));
    }

    /**
     * Return whether the specified bed block is occupied.
     *
     * @param block part of the bed
     * @return true if this bed is occupied, false if it is not
     */
    public static boolean isOccupied(GlowBlock block) {
        return (block.getData() & 0x4) == 0x4;
    }

    /**
     * Returns the head of a bed given one of its blocks.
     *
     * @param block part of the bed
     * @return The head of the bed
     */
    public static GlowBlock getHead(GlowBlock block) {
        MaterialData data = block.getState().getData();
        if (!(data instanceof Bed)) {
            return null;
        }
        Bed bed = (Bed) data;
        if (bed.isHeadOfBed()) {
            return block;
        } else {
            return block.getRelative(bed.getFacing());
        }
    }

    /**
     * Returns the foot of a bed given one of its blocks.
     *
     * @param block part of the bed
     * @return The foot of the bed
     */
    public static GlowBlock getFoot(GlowBlock block) {
        MaterialData data = block.getState().getData();
        if (!(data instanceof Bed)) {
            return null;
        }
        Bed bed = (Bed) data;
        if (bed.isHeadOfBed()) {
            return block.getRelative(bed.getFacing().getOppositeFace());
        } else {
            return block;
        }
    }

    /**
     * Returns whether a player can spawn within a block of specified material.
     *
     * @param material the material
     * @return Whether spawning is possible
     */
    public static boolean isValidSpawn(Material material) {
        switch (material) {
            case AIR:
            case SAPLING:
            case POWERED_RAIL:
            case DETECTOR_RAIL:
            case LONG_GRASS:
            case DEAD_BUSH:
            case YELLOW_FLOWER:
            case RED_ROSE:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case TORCH:
            case REDSTONE_WIRE:
            case CROPS:
            case RAILS:
            case LEVER:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case STONE_BUTTON:
            case SNOW:
            case SUGAR_CANE_BLOCK:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case VINE:
            case TRIPWIRE_HOOK:
            case TRIPWIRE:
            case FLOWER_POT:
            case WOOD_BUTTON:
            case SKULL:
            case GOLD_PLATE:
            case IRON_PLATE:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
            case ACTIVATOR_RAIL:
            case CARPET:
            case DOUBLE_PLANT:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns an 'empty' block next to the bed used to put the player at when they exit a bed or
     * respawn.
     *
     * @param head head of the bed
     * @param foot foot of the bed
     * @return Exit block or {@code null} if all spots are blocked
     */
    public static Block getExitLocation(GlowBlock head, GlowBlock foot) {
        // First check blocks near head
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block b = head.getRelative(x, 0, z);
                boolean floorValid = b.getRelative(BlockFace.DOWN).getType().isSolid();
                boolean bottomValid = isValidSpawn(b.getType());
                boolean topValid = isValidSpawn(b.getRelative(BlockFace.UP).getType());
                if (floorValid && bottomValid && topValid) {
                    return b;
                }
            }
        }

        // Then check the last three blocks near foot
        BlockFace face = head.getFace(foot);
        int modX = face.getModX();
        int modZ = face.getModZ();
        for (int x = modX == 0 ? -1 : modX; x <= (modX == 0 ? 1 : modX); x++) {
            for (int z = modZ == 0 ? -1 : modZ; z <= (modZ == 0 ? 1 : modZ); z++) {
                Block b = foot.getRelative(x, 0, z);
                boolean floorValid = b.getRelative(BlockFace.DOWN).getType().isSolid();
                boolean bottomValid = isValidSpawn(b.getType());
                boolean topValid = isValidSpawn(b.getRelative(BlockFace.UP).getType());
                if (floorValid && bottomValid && topValid) {
                    return b;
                }
            }
        }
        return null;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        BlockFace direction = getOppositeBlockFace(player.getLocation(), false).getOppositeFace();
        if (state.getBlock().getRelative(direction).getType() == Material.AIR && state.getBlock()
            .getRelative(direction).getRelative(BlockFace.DOWN).getType().isSolid()) {
            super.placeBlock(player, state, face, holding, clickedLoc);
            MaterialData data = state.getData();
            if (data instanceof Bed) {
                ((Bed) data).setFacingDirection(direction);
                ((Bed) data).setHeadOfBed(false);
                state.setData(data);
            } else {
                warnMaterialData(Bed.class, data);
            }
        }
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new BedEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
        GlowBlockState oldState) {
        if (block.getType() == Material.BED_BLOCK) {
            GlowBed bed = (GlowBed) block.getState();
            bed.setColor(DyeColor.getByWoolData(holding.getData().getData()));
            bed.update(true);
            BlockFace direction = ((Bed) bed.getData()).getFacing();
            GlowBlock headBlock = block.getRelative(direction);
            headBlock.setType(Material.BED_BLOCK);
            GlowBed headBlockState = (GlowBed) headBlock.getState();
            headBlockState.setColor(DyeColor.getByWoolData(holding.getData().getData()));
            MaterialData data = headBlockState.getData();
            ((Bed) data).setHeadOfBed(true);
            ((Bed) data).setFacingDirection(direction);
            headBlockState.setData(data);
            headBlockState.update(true);
        }
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        GlowWorld world = player.getWorld();
        MaterialData data = block.getState().getData();
        if (!(data instanceof Bed)) {
            warnMaterialData(Bed.class, data);
            return false;
        }
        block = getHead(block);

        // Disallow sleeping in nether and end biomes
        Biome biome = block.getBiome();
        if (biome == Biome.HELL || biome == Biome.SKY) {
            // Set off an explosion at the bed slightly stronger than TNT
            world.createExplosion(block.getLocation(), 5F, true);
            return true;
        }

        // Sleeping is only possible during the night or a thunderstorm
        // Tick values for day/night time taken from the minecraft wiki
        if (world.getTime() < 12541 || world.getTime() > 23458 || world.isThundering()) {
            player.sendMessage("You can only sleep at night");
            return true;
        }

        if (isOccupied(block)) {
            player.sendMessage("This bed is occupied.");
            return true;
        }

        if (!isWithinDistance(player, block, 3, 2, 3)) {
            return true; // Distance between player and bed is too great, fail silently
        }

        for (LivingEntity e : world.getLivingEntities()) {
            // Check for hostile mobs relative to the block below the head of the bed
            if (e instanceof Creature && (e.getType() != EntityType.PIG_ZOMBIE || ((PigZombie) e)
                .isAngry()) && isWithinDistance(e, block.getRelative(BlockFace.DOWN), 8, 5, 8)) {
                player.sendMessage("You may not rest now, there are monsters nearby");
                return true;
            }
        }

        player.enterBed(block);
        return true;
    }

    /**
     * Checks whether the entity is within the specified distance from the block.
     *
     * @param entity the entity
     * @param block the block
     * @param x maximum distance on x axis
     * @param y maximum distance on y axis
     * @param z maximum distance on z axis
     * @return Whether the entity is within distance
     */
    private boolean isWithinDistance(Entity entity, Block block, int x, int y, int z) {
        Location loc = entity.getLocation();
        return Math.abs(loc.getX() - block.getX()) <= x
            && Math.abs(loc.getY() - block.getY()) <= y
            && Math.abs(loc.getZ() - block.getZ()) <= z;
    }
}
