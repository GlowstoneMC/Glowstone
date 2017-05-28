package net.glowstone.generator.structures;

import net.glowstone.generator.objects.RandomItemsContent;
import net.glowstone.generator.structures.util.StructureBoundingBox;
import net.glowstone.generator.structures.util.StructureBuilder;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Chest;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.util.Vector;

import java.util.Random;

public class GlowDesertTemple extends GlowTemplePiece {

    private boolean hasPlacedChest0;
    private boolean hasPlacedChest1;
    private boolean hasPlacedChest2;
    private boolean hasPlacedChest3;

    public GlowDesertTemple() {
    }

    public GlowDesertTemple(Random random, Location location) {
        super(random, location.add(0, -18, 0), new Vector(21, 29, 21));
    }

    public boolean getHasPlacedChest0() {
        return hasPlacedChest0;
    }

    public void setHasPlacedChest0(boolean placedChest) {
        hasPlacedChest0 = placedChest;
    }

    public boolean getHasPlacedChest1() {
        return hasPlacedChest1;
    }

    public void setHasPlacedChest1(boolean placedChest) {
        hasPlacedChest1 = placedChest;
    }

    public boolean getHasPlacedChest2() {
        return hasPlacedChest2;
    }

    public void setHasPlacedChest2(boolean placedChest) {
        hasPlacedChest2 = placedChest;
    }

    public boolean getHasPlacedChest3() {
        return hasPlacedChest3;
    }

    public void setHasPlacedChest3(boolean placedChest) {
        hasPlacedChest3 = placedChest;
    }

    @Override
    public boolean generate(World world, Random random, StructureBoundingBox genBoundingBox, BlockStateDelegate delegate) {
        if (!super.generate(world, random, boundingBox, delegate)) {
            return false;
        }

        StructureBuilder builder = new StructureBuilder(world, this, genBoundingBox, delegate);
        for (int x = 0; x < 21; x++) {
            for (int z = 0; z < 21; z++) {
                builder.setBlockDownward(new Vector(x, 13, z), Material.SANDSTONE);
            }
        }
        builder.fill(new Vector(0, 14, 0), new Vector(20, 18, 20), Material.SANDSTONE);
        for (int i = 1; i <= 9; i++) {
            builder.fill(new Vector(i, i + 18, i), new Vector(20 - i, i + 18, 20 - i), Material.SANDSTONE);
            builder.fill(new Vector(i + 1, i + 18, i + 1), new Vector(19 - i, i + 18, 19 - i), Material.AIR);
        }
        // east tower
        builder.fill(new Vector(0, 18, 0), new Vector(4, 27, 4), Material.SANDSTONE, Material.AIR);
        builder.fill(new Vector(1, 28, 1), new Vector(3, 28, 3), Material.SANDSTONE);
        Stairs stairsN = new Stairs(Material.SANDSTONE_STAIRS);
        stairsN.setFacingDirection(getRelativeFacing(BlockFace.SOUTH));
        builder.setBlock(new Vector(2, 28, 0), stairsN.getItemType(), stairsN);
        Stairs stairsE = new Stairs(Material.SANDSTONE_STAIRS);
        stairsE.setFacingDirection(getRelativeFacing(BlockFace.WEST));
        builder.setBlock(new Vector(4, 28, 2), stairsE.getItemType(), stairsE);
        Stairs stairsS = new Stairs(Material.SANDSTONE_STAIRS);
        stairsS.setFacingDirection(getRelativeFacing(BlockFace.NORTH));
        builder.setBlock(new Vector(2, 28, 4), stairsS.getItemType(), stairsS);
        Stairs stairsW = new Stairs(Material.SANDSTONE_STAIRS);
        stairsW.setFacingDirection(getRelativeFacing(BlockFace.EAST));
        builder.setBlock(new Vector(0, 28, 2), stairsW.getItemType(), stairsW);
        builder.fill(new Vector(1, 19, 5), new Vector(3, 22, 11), Material.SANDSTONE);
        builder.fill(new Vector(2, 22, 4), new Vector(2, 24, 4), Material.AIR);
        builder.fill(new Vector(1, 19, 3), new Vector(2, 20, 3), Material.SANDSTONE);
        builder.setBlock(new Vector(1, 19, 2), Material.SANDSTONE);
        Step step = new Step(Material.SANDSTONE);
        builder.setBlock(new Vector(1, 20, 2), step.getItemType(), step);
        builder.setBlock(new Vector(2, 19, 2), stairsE.getItemType(), stairsE);
        for (int i = 0; i < 2; i++) {
            builder.setBlock(new Vector(2, 21 + i, 4 + i), stairsN.getItemType(), stairsN);
        }
        // west tower
        builder.fill(new Vector(16, 18, 0), new Vector(20, 27, 4), Material.SANDSTONE, Material.AIR);
        builder.fill(new Vector(17, 28, 1), new Vector(19, 28, 3), Material.SANDSTONE);
        builder.setBlock(new Vector(18, 28, 0), stairsN.getItemType(), stairsN);
        builder.setBlock(new Vector(20, 28, 2), stairsE.getItemType(), stairsE);
        builder.setBlock(new Vector(18, 28, 4), stairsS.getItemType(), stairsS);
        builder.setBlock(new Vector(16, 28, 2), stairsW.getItemType(), stairsW);
        builder.fill(new Vector(17, 19, 5), new Vector(19, 22, 11), Material.SANDSTONE);
        builder.fill(new Vector(18, 22, 4), new Vector(18, 24, 4), Material.AIR);
        builder.fill(new Vector(18, 19, 3), new Vector(19, 20, 3), Material.SANDSTONE);
        builder.setBlock(new Vector(19, 19, 2), Material.SANDSTONE);
        builder.setBlock(new Vector(19, 20, 2), step.getItemType(), step);
        builder.setBlock(new Vector(18, 19, 2), stairsW.getItemType(), stairsW);
        for (int i = 0; i < 2; i++) {
            builder.setBlock(new Vector(18, 21 + i, 4 + i), stairsN.getItemType(), stairsN);
        }
        // tower symbols
        for (int i = 0; i < 2; i++) {
            // front
            builder.fill(new Vector(1 + (i << 4), 20, 0), new Vector(1 + (i << 4), 21, 0), Material.SANDSTONE, 2);
            builder.fill(new Vector(2 + (i << 4), 20, 0), new Vector(2 + (i << 4), 21, 0), Material.STAINED_CLAY, 1);
            builder.fill(new Vector(3 + (i << 4), 20, 0), new Vector(3 + (i << 4), 21, 0), Material.SANDSTONE, 2);
            builder.setBlock(new Vector(1 + (i << 4), 22, 0), Material.STAINED_CLAY, 1);
            builder.setBlock(new Vector(2 + (i << 4), 22, 0), Material.SANDSTONE, 1);
            builder.setBlock(new Vector(3 + (i << 4), 22, 0), Material.STAINED_CLAY, 1);
            builder.setBlock(new Vector(1 + (i << 4), 23, 0), Material.SANDSTONE, 2);
            builder.setBlock(new Vector(2 + (i << 4), 23, 0), Material.STAINED_CLAY, 1);
            builder.setBlock(new Vector(3 + (i << 4), 23, 0), Material.SANDSTONE, 2);
            builder.setBlock(new Vector(1 + (i << 4), 24, 0), Material.STAINED_CLAY, 1);
            builder.setBlock(new Vector(2 + (i << 4), 24, 0), Material.SANDSTONE, 1);
            builder.setBlock(new Vector(3 + (i << 4), 24, 0), Material.STAINED_CLAY, 1);
            builder.fill(new Vector(1 + (i << 4), 25, 0), new Vector(3 + (i << 4), 25, 0), Material.STAINED_CLAY, 1);
            builder.fill(new Vector(1 + (i << 4), 26, 0), new Vector(3 + (i << 4), 26, 0), Material.SANDSTONE, 2);
            // side
            builder.fill(new Vector(i * 20, 20, 1), new Vector(i * 20, 21, 1), Material.SANDSTONE, 2);
            builder.fill(new Vector(i * 20, 20, 2), new Vector(i * 20, 21, 2), Material.STAINED_CLAY, 1);
            builder.fill(new Vector(i * 20, 20, 3), new Vector(i * 20, 21, 3), Material.SANDSTONE, 2);
            builder.setBlock(new Vector(i * 20, 22, 1), Material.STAINED_CLAY, 1);
            builder.setBlock(new Vector(i * 20, 22, 2), Material.SANDSTONE, 1);
            builder.setBlock(new Vector(i * 20, 22, 3), Material.STAINED_CLAY, 1);
            builder.setBlock(new Vector(i * 20, 23, 1), Material.SANDSTONE, 2);
            builder.setBlock(new Vector(i * 20, 23, 2), Material.STAINED_CLAY, 1);
            builder.setBlock(new Vector(i * 20, 23, 3), Material.SANDSTONE, 2);
            builder.setBlock(new Vector(i * 20, 24, 1), Material.STAINED_CLAY, 1);
            builder.setBlock(new Vector(i * 20, 24, 2), Material.SANDSTONE, 1);
            builder.setBlock(new Vector(i * 20, 24, 3), Material.STAINED_CLAY, 1);
            builder.fill(new Vector(i * 20, 25, 1), new Vector(i * 20, 25, 3), Material.STAINED_CLAY, 1);
            builder.fill(new Vector(i * 20, 26, 1), new Vector(i * 20, 26, 3), Material.SANDSTONE, 2);
        }
        // front entrance
        builder.fill(new Vector(8, 18, 1), new Vector(12, 22, 4), Material.SANDSTONE, Material.AIR);
        builder.fill(new Vector(9, 19, 0), new Vector(11, 21, 4), Material.AIR);
        builder.fill(new Vector(9, 19, 1), new Vector(9, 20, 1), Material.SANDSTONE, 2);
        builder.fill(new Vector(11, 19, 1), new Vector(11, 20, 1), Material.SANDSTONE, 2);
        builder.fill(new Vector(9, 21, 1), new Vector(11, 21, 1), Material.SANDSTONE, 2);
        builder.fill(new Vector(8, 18, 0), new Vector(8, 21, 0), Material.SANDSTONE);
        builder.fill(new Vector(12, 18, 0), new Vector(12, 21, 0), Material.SANDSTONE);
        builder.fill(new Vector(8, 22, 0), new Vector(12, 22, 0), Material.SANDSTONE, 2);
        builder.setBlock(new Vector(8, 23, 0), Material.SANDSTONE, 2);
        builder.setBlock(new Vector(9, 23, 0), Material.STAINED_CLAY, 1);
        builder.setBlock(new Vector(10, 23, 0), Material.SANDSTONE, 1);
        builder.setBlock(new Vector(11, 23, 0), Material.STAINED_CLAY, 1);
        builder.setBlock(new Vector(12, 23, 0), Material.SANDSTONE, 2);
        builder.fill(new Vector(9, 24, 0), new Vector(11, 24, 0), Material.SANDSTONE, 2);
        // east entrance
        builder.fill(new Vector(5, 23, 9), new Vector(5, 25, 11), Material.SANDSTONE, 2);
        builder.fill(new Vector(6, 25, 9), new Vector(6, 25, 11), Material.SANDSTONE);
        builder.fill(new Vector(5, 23, 10), new Vector(6, 24, 10), Material.AIR);
        // west entrance
        builder.fill(new Vector(15, 23, 9), new Vector(15, 25, 11), Material.SANDSTONE, 2);
        builder.fill(new Vector(14, 25, 9), new Vector(14, 25, 11), Material.SANDSTONE);
        builder.fill(new Vector(14, 23, 10), new Vector(15, 24, 10), Material.AIR);
        // corridor to east tower
        builder.fill(new Vector(4, 19, 1), new Vector(8, 21, 3), Material.SANDSTONE, Material.AIR);
        builder.fill(new Vector(4, 19, 2), new Vector(8, 20, 2), Material.AIR);
        // corridor to west tower
        builder.fill(new Vector(12, 19, 1), new Vector(16, 21, 3), Material.SANDSTONE, Material.AIR);
        builder.fill(new Vector(12, 19, 2), new Vector(16, 20, 2), Material.AIR);
        // pillars in the middle of 1st floor
        builder.fill(new Vector(8, 19, 8), new Vector(8, 21, 8), Material.SANDSTONE, 2);
        builder.fill(new Vector(12, 19, 8), new Vector(12, 21, 8), Material.SANDSTONE, 2);
        builder.fill(new Vector(12, 19, 12), new Vector(12, 21, 12), Material.SANDSTONE, 2);
        builder.fill(new Vector(8, 19, 12), new Vector(8, 21, 12), Material.SANDSTONE, 2);
        // 2nd floor
        builder.fill(new Vector(5, 22, 5), new Vector(15, 22, 15), Material.SANDSTONE);
        builder.fill(new Vector(9, 22, 9), new Vector(11, 22, 11), Material.AIR);
        // east and west corridors
        builder.fill(new Vector(3, 19, 5), new Vector(3, 20, 11), Material.AIR);
        builder.fill(new Vector(4, 21, 5), new Vector(4, 21, 16), Material.SANDSTONE);
        builder.fill(new Vector(17, 19, 5), new Vector(17, 20, 11), Material.AIR);
        builder.fill(new Vector(16, 21, 5), new Vector(16, 21, 16), Material.SANDSTONE);
        builder.fill(new Vector(2, 19, 12), new Vector(2, 19, 18), Material.SANDSTONE);
        builder.fill(new Vector(18, 19, 12), new Vector(18, 19, 18), Material.SANDSTONE);
        builder.fill(new Vector(3, 19, 18), new Vector(18, 19, 18), Material.SANDSTONE);
        for (int i = 0; i < 7; i++) {
            builder.setBlock(new Vector(4, 19, 5 + (i << 1)), Material.SANDSTONE, 2);
            builder.setBlock(new Vector(4, 20, 5 + (i << 1)), Material.SANDSTONE, 1);
            builder.setBlock(new Vector(16, 19, 5 + (i << 1)), Material.SANDSTONE, 2);
            builder.setBlock(new Vector(16, 20, 5 + (i << 1)), Material.SANDSTONE, 1);
        }
        // floor symbols
        builder.setBlock(new Vector(9, 18, 9), Material.STAINED_CLAY, 1);
        builder.setBlock(new Vector(11, 18, 9), Material.STAINED_CLAY, 1);
        builder.setBlock(new Vector(11, 18, 11), Material.STAINED_CLAY, 1);
        builder.setBlock(new Vector(9, 18, 11), Material.STAINED_CLAY, 1);
        builder.setBlock(new Vector(10, 18, 10), Material.STAINED_CLAY, 11);
        builder.fill(new Vector(10, 18, 7), new Vector(10, 18, 8), Material.STAINED_CLAY, 1);
        builder.fill(new Vector(12, 18, 10), new Vector(13, 18, 10), Material.STAINED_CLAY, 1);
        builder.fill(new Vector(10, 18, 12), new Vector(10, 18, 13), Material.STAINED_CLAY, 1);
        builder.fill(new Vector(7, 18, 10), new Vector(8, 18, 10), Material.STAINED_CLAY, 1);
        // trap chamber
        builder.fill(new Vector(8, 0, 8), new Vector(12, 3, 12), Material.SANDSTONE, 2);
        builder.fill(new Vector(8, 4, 8), new Vector(12, 4, 12), Material.SANDSTONE, 1);
        builder.fill(new Vector(8, 5, 8), new Vector(12, 5, 12), Material.SANDSTONE, 2);
        builder.fill(new Vector(8, 6, 8), new Vector(12, 13, 12), Material.SANDSTONE);
        builder.fill(new Vector(9, 3, 9), new Vector(11, 17, 11), Material.AIR);
        builder.fill(new Vector(9, 1, 9), new Vector(11, 1, 11), Material.TNT);
        builder.fill(new Vector(9, 2, 9), new Vector(11, 2, 11), Material.SANDSTONE, 2);
        builder.fill(new Vector(10, 3, 8), new Vector(10, 4, 8), Material.AIR);
        builder.setBlock(new Vector(10, 3, 7), Material.SANDSTONE, 2);
        builder.setBlock(new Vector(10, 4, 7), Material.SANDSTONE, 1);
        builder.fill(new Vector(12, 3, 10), new Vector(12, 4, 10), Material.AIR);
        builder.setBlock(new Vector(13, 3, 10), Material.SANDSTONE, 2);
        builder.setBlock(new Vector(13, 4, 10), Material.SANDSTONE, 1);
        builder.fill(new Vector(10, 3, 12), new Vector(10, 4, 12), Material.AIR);
        builder.setBlock(new Vector(10, 3, 13), Material.SANDSTONE, 2);
        builder.setBlock(new Vector(10, 4, 13), Material.SANDSTONE, 1);
        builder.fill(new Vector(8, 3, 10), new Vector(8, 4, 10), Material.AIR);
        builder.setBlock(new Vector(7, 3, 10), Material.SANDSTONE, 2);
        builder.setBlock(new Vector(7, 4, 10), Material.SANDSTONE, 1);
        builder.setBlock(new Vector(10, 3, 10), Material.STONE_PLATE);

        RandomItemsContent chestContent = getChestContent();
        if (!hasPlacedChest0) {
            hasPlacedChest0 = builder.createRandomItemsContainer(new Vector(10, 3, 12), random, chestContent, new Chest(getRelativeFacing(BlockFace.NORTH)), random.nextInt(5) + 2);
        }
        if (!hasPlacedChest1) {
            hasPlacedChest1 = builder.createRandomItemsContainer(new Vector(8, 3, 10), random, chestContent, new Chest(getRelativeFacing(BlockFace.EAST)), random.nextInt(5) + 2);
        }
        if (!hasPlacedChest2) {
            hasPlacedChest2 = builder.createRandomItemsContainer(new Vector(10, 3, 8), random, chestContent, new Chest(getRelativeFacing(BlockFace.SOUTH)), random.nextInt(5) + 2);
        }
        if (!hasPlacedChest3) {
            hasPlacedChest3 = builder.createRandomItemsContainer(new Vector(12, 3, 10), random, chestContent, new Chest(getRelativeFacing(BlockFace.WEST)), random.nextInt(5) + 2);
        }

        return true;
    }
}
