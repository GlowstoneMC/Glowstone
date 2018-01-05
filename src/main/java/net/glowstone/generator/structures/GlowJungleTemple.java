package net.glowstone.generator.structures;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.glowstone.generator.objects.RandomItemsContent;
import net.glowstone.generator.objects.RandomItemsContent.RandomAmountItem;
import net.glowstone.generator.structures.util.StructureBoundingBox;
import net.glowstone.generator.structures.util.StructureBuilder;
import net.glowstone.generator.structures.util.StructureBuilder.StructureMaterial;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Chest;
import org.bukkit.material.Diode;
import org.bukkit.material.Dispenser;
import org.bukkit.material.Lever;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.Stairs;
import org.bukkit.material.TripwireHook;
import org.bukkit.material.Vine;
import org.bukkit.util.Vector;

public class GlowJungleTemple extends GlowTemplePiece {

    private boolean placedTrap1;
    private boolean placedTrap2;
    private boolean placedMainChest;
    private boolean placedHiddenChest;

    public GlowJungleTemple() {
    }

    public GlowJungleTemple(Random random, Location location) {
        super(random, location, new Vector(12, 14, 15));
    }

    public boolean getHasPlacedTrap1() {
        return placedTrap1;
    }

    public void setHasPlacedTrap1(boolean placedTrap) {
        placedTrap1 = placedTrap;
    }

    public boolean getHasPlacedTrap2() {
        return placedTrap2;
    }

    public void setHasPlacedTrap2(boolean placedTrap) {
        placedTrap2 = placedTrap;
    }

    public boolean getHasPlacedMainChest() {
        return placedMainChest;
    }

    public void setHasPlacedMainChest(boolean placedChest) {
        placedMainChest = placedChest;
    }

    public boolean getHasPlacedHiddenChest() {
        return placedHiddenChest;
    }

    public void setHasPlacedHiddenChest(boolean placedChest) {
        placedHiddenChest = placedChest;
    }

    @Override
    public boolean generate(World world, Random random, StructureBoundingBox genBoundingBox,
        BlockStateDelegate delegate) {
        if (!super.generate(world, random, boundingBox, delegate)) {
            return false;
        }

        adjustHorizPos(world);

        boundingBox.offset(new Vector(0, -4, 0));

        StructureBuilder builder = new StructureBuilder(world, this, genBoundingBox, delegate);
        Map<StructureMaterial, Integer> stones = new HashMap<>();
        builder.addRandomMaterial(stones, 4, Material.COBBLESTONE, 0);
        builder.addRandomMaterial(stones, 6, Material.MOSSY_COBBLESTONE, 0);

        RandomItemsContent chestContent = getChestContent();
        RandomItemsContent dispenserContent = new RandomItemsContent();
        dispenserContent.addItem(new RandomAmountItem(Material.ARROW, 2, 7), 30);

        // 1st floor
        builder.fillWithRandomMaterial(new Vector(0, 0, 0), new Vector(11, 0, 14), random, stones);
        builder.fillWithRandomMaterial(new Vector(0, 1, 0), new Vector(11, 3, 0), random, stones);
        builder.fillWithRandomMaterial(new Vector(11, 1, 1), new Vector(11, 3, 13), random, stones);
        builder.fillWithRandomMaterial(new Vector(0, 1, 1), new Vector(0, 3, 13), random, stones);
        builder.fillWithRandomMaterial(new Vector(0, 1, 14), new Vector(11, 3, 14), random, stones);
        builder.fillWithRandomMaterial(new Vector(0, 4, 0), new Vector(11, 4, 14), random, stones);
        Stairs entranceStairs = new Stairs(Material.COBBLESTONE_STAIRS);
        entranceStairs.setFacingDirection(getRelativeFacing(BlockFace.SOUTH));
        builder.fill(new Vector(4, 4, 0), new Vector(7, 4, 0), entranceStairs.getItemType(),
            entranceStairs);
        builder.fill(new Vector(1, 1, 1), new Vector(10, 3, 13), Material.AIR);
        builder.fill(new Vector(5, 4, 7), new Vector(6, 4, 9), Material.AIR);

        // 2nd floor
        builder.fillWithRandomMaterial(new Vector(2, 5, 2), new Vector(9, 6, 2), random, stones);
        builder.fillWithRandomMaterial(new Vector(9, 5, 3), new Vector(9, 6, 11), random, stones);
        builder.fillWithRandomMaterial(new Vector(2, 5, 12), new Vector(9, 6, 12), random, stones);
        builder.fillWithRandomMaterial(new Vector(2, 5, 3), new Vector(2, 6, 11), random, stones);
        builder.fillWithRandomMaterial(new Vector(1, 7, 1), new Vector(10, 7, 13), random, stones);
        builder.fill(new Vector(3, 5, 3), new Vector(8, 6, 11), Material.AIR);
        builder.fill(new Vector(4, 7, 6), new Vector(7, 7, 9), Material.AIR);
        builder.fill(new Vector(5, 5, 2), new Vector(6, 6, 2), Material.AIR);
        builder.fill(new Vector(5, 6, 12), new Vector(6, 6, 12), Material.AIR);

        // 3rd floor
        builder.fillWithRandomMaterial(new Vector(1, 8, 1), new Vector(10, 9, 1), random, stones);
        builder.fillWithRandomMaterial(new Vector(10, 8, 2), new Vector(10, 9, 12), random, stones);
        builder.fillWithRandomMaterial(new Vector(1, 8, 13), new Vector(10, 9, 13), random, stones);
        builder.fillWithRandomMaterial(new Vector(1, 8, 2), new Vector(1, 9, 12), random, stones);
        builder.fill(new Vector(2, 8, 2), new Vector(9, 9, 12), Material.AIR);
        builder.fill(new Vector(5, 9, 1), new Vector(6, 9, 1), Material.AIR);
        builder.fill(new Vector(5, 9, 13), new Vector(6, 9, 13), Material.AIR);
        builder.setBlock(new Vector(10, 9, 5), Material.AIR);
        builder.setBlock(new Vector(10, 9, 9), Material.AIR);
        builder.setBlock(new Vector(1, 9, 5), Material.AIR);
        builder.setBlock(new Vector(1, 9, 9), Material.AIR);

        // roof
        builder.fillWithRandomMaterial(new Vector(1, 10, 1), new Vector(10, 10, 4), random, stones);
        builder.fillWithRandomMaterial(new Vector(8, 10, 5), new Vector(10, 10, 9), random, stones);
        builder.fillWithRandomMaterial(new Vector(1, 10, 5), new Vector(3, 10, 9), random, stones);
        builder
            .fillWithRandomMaterial(new Vector(1, 10, 10), new Vector(10, 10, 13), random, stones);
        builder.fillWithRandomMaterial(new Vector(3, 11, 3), new Vector(8, 11, 5), random, stones);
        builder.fillWithRandomMaterial(new Vector(7, 11, 6), new Vector(8, 11, 8), random, stones);
        builder.fillWithRandomMaterial(new Vector(3, 11, 6), new Vector(4, 11, 8), random, stones);
        builder.fillWithRandomMaterial(new Vector(3, 11, 9), new Vector(8, 11, 11), random, stones);
        builder.fillWithRandomMaterial(new Vector(4, 12, 4), new Vector(7, 12, 10), random, stones);
        builder.fill(new Vector(4, 10, 5), new Vector(7, 10, 9), Material.AIR);
        builder.fill(new Vector(5, 11, 6), new Vector(6, 11, 8), Material.AIR);

        // outside walls decorations
        builder.fillWithRandomMaterial(new Vector(2, 8, 0), new Vector(2, 9, 0), random, stones);
        builder.fillWithRandomMaterial(new Vector(4, 8, 0), new Vector(4, 9, 0), random, stones);
        builder.fillWithRandomMaterial(new Vector(7, 8, 0), new Vector(7, 9, 0), random, stones);
        builder.fillWithRandomMaterial(new Vector(9, 8, 0), new Vector(9, 9, 0), random, stones);
        builder.fillWithRandomMaterial(new Vector(5, 10, 0), new Vector(6, 10, 0), random, stones);
        for (int i = 0; i < 6; i++) {
            builder.fillWithRandomMaterial(new Vector(11, 8, 2 + (i << 1)),
                new Vector(11, 9, 2 + (i << 1)), random, stones);
            builder.fillWithRandomMaterial(new Vector(0, 8, 2 + (i << 1)),
                new Vector(0, 9, 2 + (i << 1)), random, stones);
        }
        builder.setBlockWithRandomMaterial(new Vector(11, 10, 5), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(11, 10, 9), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(0, 10, 5), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(0, 10, 9), random, stones);
        builder.fillWithRandomMaterial(new Vector(2, 8, 14), new Vector(2, 9, 14), random, stones);
        builder.fillWithRandomMaterial(new Vector(4, 8, 14), new Vector(4, 9, 14), random, stones);
        builder.fillWithRandomMaterial(new Vector(7, 8, 14), new Vector(7, 9, 14), random, stones);
        builder.fillWithRandomMaterial(new Vector(9, 8, 14), new Vector(9, 9, 14), random, stones);

        // roof decorations
        builder.fillWithRandomMaterial(new Vector(2, 11, 2), new Vector(2, 13, 2), random, stones);
        builder.fillWithRandomMaterial(new Vector(9, 11, 2), new Vector(9, 13, 2), random, stones);
        builder
            .fillWithRandomMaterial(new Vector(9, 11, 12), new Vector(9, 13, 12), random, stones);
        builder
            .fillWithRandomMaterial(new Vector(2, 11, 12), new Vector(2, 13, 12), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(4, 13, 4), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(7, 13, 4), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(7, 13, 10), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(4, 13, 10), random, stones);
        Stairs roofStairsN = new Stairs(Material.COBBLESTONE_STAIRS);
        roofStairsN.setFacingDirection(getRelativeFacing(BlockFace.SOUTH));
        builder.fill(new Vector(5, 13, 6), new Vector(6, 13, 6), roofStairsN.getItemType(),
            roofStairsN);
        builder.fillWithRandomMaterial(new Vector(5, 13, 7), new Vector(6, 13, 7), random, stones);
        Stairs roofStairsS = new Stairs(Material.COBBLESTONE_STAIRS);
        roofStairsS.setFacingDirection(getRelativeFacing(BlockFace.NORTH));
        builder.fill(new Vector(5, 13, 8), new Vector(6, 13, 8), roofStairsS.getItemType(),
            roofStairsS);

        // 1st floor inside
        for (int i = 0; i < 6; i++) {
            builder.fillWithRandomMaterial(new Vector(1, 3, 2 + (i << 1)),
                new Vector(3, 3, 2 + (i << 1)), random, stones);
        }
        for (int i = 0; i < 7; i++) {
            builder.fillWithRandomMaterial(new Vector(1, 1, 1 + (i << 1)),
                new Vector(1, 2, 1 + (i << 1)), random, stones);
        }
        builder.setBlockWithRandomMaterial(new Vector(2, 2, 1), random, stones);
        builder.setBlock(new Vector(3, 1, 1), Material.MOSSY_COBBLESTONE);
        builder.fillWithRandomMaterial(new Vector(4, 2, 1), new Vector(5, 2, 1), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(6, 1, 1), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(6, 3, 1), random, stones);
        builder.fillWithRandomMaterial(new Vector(7, 2, 1), new Vector(9, 2, 1), random, stones);
        builder.setBlock(new Vector(8, 1, 1), Material.MOSSY_COBBLESTONE);
        builder.fillWithRandomMaterial(new Vector(10, 1, 1), new Vector(10, 3, 7), random, stones);
        builder.fillWithRandomMaterial(new Vector(9, 3, 1), new Vector(9, 3, 7), random, stones);
        builder.setBlock(new Vector(9, 1, 2), Material.MOSSY_COBBLESTONE);
        builder.setBlock(new Vector(9, 1, 4), Material.MOSSY_COBBLESTONE);
        builder.setBlock(new Vector(8, 1, 5), Material.MOSSY_COBBLESTONE);
        builder.fill(new Vector(7, 2, 5), new Vector(7, 3, 5), Material.MOSSY_COBBLESTONE);
        builder.setBlock(new Vector(6, 1, 5), Material.MOSSY_COBBLESTONE);
        builder.setBlockWithRandomMaterial(new Vector(6, 2, 5), random, stones);
        builder.fill(new Vector(5, 2, 5), new Vector(5, 3, 5), Material.MOSSY_COBBLESTONE);
        builder.setBlock(new Vector(4, 1, 5), Material.MOSSY_COBBLESTONE);
        builder.fillWithRandomMaterial(new Vector(7, 1, 6), new Vector(7, 3, 11), random, stones);
        builder.fillWithRandomMaterial(new Vector(4, 1, 6), new Vector(4, 3, 11), random, stones);
        builder.fillWithRandomMaterial(new Vector(5, 3, 11), new Vector(6, 3, 11), random, stones);
        builder.fillWithRandomMaterial(new Vector(8, 3, 11), new Vector(10, 3, 11), random, stones);
        builder.fillWithRandomMaterial(new Vector(8, 1, 11), new Vector(10, 1, 11), random, stones);
        builder.fillWithRandomMaterial(new Vector(5, 1, 8), new Vector(6, 1, 8), random, stones);
        builder.fillWithRandomMaterial(new Vector(6, 1, 7), new Vector(6, 2, 7), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(5, 2, 7), random, stones);
        builder.fillWithRandomMaterial(new Vector(6, 1, 6), new Vector(6, 3, 6), random, stones);
        builder.fillWithRandomMaterial(new Vector(5, 2, 6), new Vector(5, 3, 6), random, stones);
        builder.fillWithRandomMaterial(new Vector(8, 2, 6), new Vector(9, 2, 6), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(8, 3, 6), random, stones);
        builder.fillWithRandomMaterial(new Vector(9, 1, 7), new Vector(9, 2, 7), random, stones);
        builder.fillWithRandomMaterial(new Vector(8, 1, 7), new Vector(8, 3, 7), random, stones);
        builder.fillWithRandomMaterial(new Vector(10, 1, 8), new Vector(10, 1, 10), random, stones);
        builder.setBlock(new Vector(10, 2, 9), Material.MOSSY_COBBLESTONE);
        builder.fillWithRandomMaterial(new Vector(8, 1, 8), new Vector(8, 1, 10), random, stones);
        builder.fill(new Vector(8, 2, 11), new Vector(10, 2, 11), Material.SMOOTH_BRICK, 3);
        Lever lever = new Lever(Material.LEVER,
            (byte) 4); // workaround for bukkit, can't set an attached BlockFace
        lever.setFacingDirection(getRelativeFacing(BlockFace.SOUTH));
        builder.fill(new Vector(8, 2, 12), new Vector(10, 2, 12), lever.getItemType(), lever);
        if (!placedTrap1) {
            placedTrap1 = builder
                .createRandomItemsContainer(new Vector(3, 2, 1), random, dispenserContent,
                    new Dispenser(getRelativeFacing(BlockFace.SOUTH)), 2);
        }
        if (!placedTrap2) {
            placedTrap2 = builder
                .createRandomItemsContainer(new Vector(9, 2, 3), random, dispenserContent,
                    new Dispenser(getRelativeFacing(BlockFace.WEST)), 2);
        }
        Vine vine = new Vine(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
        builder.setBlock(new Vector(3, 2, 2), vine.getItemType(), vine);
        builder.fill(new Vector(8, 2, 3), new Vector(8, 3, 3), vine.getItemType(), vine);
        builder.fill(new Vector(2, 1, 8), new Vector(3, 1, 8), Material.TRIPWIRE);
        TripwireHook hookE = new TripwireHook(getRelativeFacing(BlockFace.WEST));
        hookE.setConnected(true);
        builder.setBlock(new Vector(4, 1, 8), hookE.getItemType(), hookE);
        TripwireHook hookW = new TripwireHook(getRelativeFacing(BlockFace.EAST));
        hookW.setConnected(true);
        builder.setBlock(new Vector(1, 1, 8), hookW.getItemType(), hookW);
        builder.fill(new Vector(5, 1, 1), new Vector(5, 1, 7), Material.REDSTONE_WIRE);
        builder.setBlock(new Vector(4, 1, 1), Material.REDSTONE_WIRE);
        builder.fill(new Vector(7, 1, 2), new Vector(7, 1, 4), Material.TRIPWIRE);
        TripwireHook hookN = new TripwireHook(getRelativeFacing(BlockFace.SOUTH));
        hookN.setConnected(true);
        builder.setBlock(new Vector(7, 1, 1), hookN.getItemType(), hookN);
        TripwireHook hookS = new TripwireHook(getRelativeFacing(BlockFace.NORTH));
        hookS.setConnected(true);
        builder.setBlock(new Vector(7, 1, 5), hookS.getItemType(), hookS);
        builder.fill(new Vector(8, 1, 6), new Vector(9, 1, 6), Material.REDSTONE_WIRE);
        builder.setBlock(new Vector(9, 1, 5), Material.REDSTONE_WIRE);
        builder.setBlock(new Vector(9, 2, 4), Material.REDSTONE_WIRE);
        PistonBaseMaterial pistonE = new PistonBaseMaterial(Material.PISTON_STICKY_BASE);
        pistonE.setFacingDirection(getRelativeFacing(BlockFace.WEST));
        builder.fill(new Vector(10, 2, 8), new Vector(10, 3, 8), pistonE.getItemType(), pistonE);
        PistonBaseMaterial pistonUp = new PistonBaseMaterial(Material.PISTON_STICKY_BASE);
        pistonUp.setFacingDirection(BlockFace.UP);
        builder.setBlock(new Vector(9, 2, 8), pistonUp.getItemType(), pistonUp);
        builder.setBlock(new Vector(10, 3, 9), Material.REDSTONE_WIRE);
        builder.fill(new Vector(8, 2, 9), new Vector(8, 2, 10), Material.REDSTONE_WIRE);
        Diode repeater = new Diode(Material.DIODE_BLOCK_OFF);
        repeater.setDelay(1);
        repeater.setFacingDirection(getRelativeFacing(BlockFace.SOUTH));
        builder.setBlock(new Vector(10, 2, 10), repeater.getItemType(), repeater);
        if (!placedMainChest) {
            placedMainChest = builder
                .createRandomItemsContainer(new Vector(8, 1, 3), random, chestContent,
                    new Chest(getRelativeFacing(BlockFace.WEST)), random.nextInt(5) + 2);
        }
        if (!placedHiddenChest) {
            placedHiddenChest = builder
                .createRandomItemsContainer(new Vector(9, 1, 10), random, chestContent,
                    new Chest(getRelativeFacing(BlockFace.NORTH)), random.nextInt(5) + 2);
        }

        // 2nd floor inside
        for (int i = 0; i < 4; i++) {
            Stairs stairsS = new Stairs(Material.COBBLESTONE_STAIRS);
            stairsS.setFacingDirection(getRelativeFacing(BlockFace.NORTH));
            builder.fill(new Vector(5, 4 - i, 6 + i), new Vector(6, 4 - i, 6 + i),
                stairsS.getItemType(), stairsS);
        }
        builder.fillWithRandomMaterial(new Vector(4, 5, 10), new Vector(7, 6, 10), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(4, 5, 9), random, stones);
        builder.setBlockWithRandomMaterial(new Vector(7, 5, 9), random, stones);
        for (int i = 0; i < 3; i++) {
            Stairs leftStairs = new Stairs(Material.COBBLESTONE_STAIRS);
            leftStairs.setFacingDirection(getRelativeFacing(BlockFace.SOUTH));
            builder.setBlock(new Vector(7, 5 + i, 8 + i), leftStairs.getItemType(), leftStairs);
            Stairs rightStairs = new Stairs(Material.COBBLESTONE_STAIRS);
            rightStairs.setFacingDirection(getRelativeFacing(BlockFace.SOUTH));
            builder.setBlock(new Vector(4, 5 + i, 8 + i), rightStairs.getItemType(), rightStairs);
        }

        // 3rd floor inside
        builder.fillWithRandomMaterial(new Vector(5, 8, 5), new Vector(6, 8, 5), random, stones);
        Stairs stairsE = new Stairs(Material.COBBLESTONE_STAIRS);
        stairsE.setFacingDirection(getRelativeFacing(BlockFace.WEST));
        builder.setBlock(new Vector(7, 8, 5), stairsE.getItemType(), stairsE);
        Stairs stairsW = new Stairs(Material.COBBLESTONE_STAIRS);
        stairsW.setFacingDirection(getRelativeFacing(BlockFace.EAST));
        builder.setBlock(new Vector(4, 8, 5), stairsW.getItemType(), stairsW);

        return true;
    }
}
