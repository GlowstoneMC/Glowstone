package net.glowstone.block.blocktype;

import com.google.common.collect.ImmutableMap;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.SkullEntity;
import net.glowstone.block.entity.state.GlowSkull;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.pattern.BlockPattern;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Skull;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;

import static org.bukkit.Material.SOUL_SAND;
import static org.bukkit.Material.WITHER_SKELETON_SKULL;

public class BlockSkull extends BlockType {

    private static final ImmutableMap<SkullType, Material> SKULL_MATERIALS;
    private static final BlockPattern WITHER_PATTERN = new BlockPattern(
        new BlockPattern.PatternItem(WITHER_SKELETON_SKULL, (byte) 1, 0, 0),
        new BlockPattern.PatternItem(WITHER_SKELETON_SKULL, (byte) 1, 1, 0),
        new BlockPattern.PatternItem(WITHER_SKELETON_SKULL, (byte) 1, 2, 0),
        new BlockPattern.PatternItem(SOUL_SAND, (byte) 0, 0, 1),
        new BlockPattern.PatternItem(SOUL_SAND, (byte) 0, 1, 1),
        new BlockPattern.PatternItem(SOUL_SAND, (byte) 0, 2, 1),
        new BlockPattern.PatternItem(SOUL_SAND, (byte) 0, 1, 2)
    );

    static {
        EnumMap<SkullType, Material> skullTypesBuilder = new EnumMap<>(SkullType.class);
        skullTypesBuilder.put(SkullType.CREEPER, Material.CREEPER_HEAD);
        skullTypesBuilder.put(SkullType.DRAGON, Material.DRAGON_HEAD);
        skullTypesBuilder.put(SkullType.PLAYER, Material.PLAYER_HEAD);
        skullTypesBuilder.put(SkullType.ZOMBIE, Material.ZOMBIE_HEAD);
        skullTypesBuilder.put(SkullType.SKELETON, Material.SKELETON_SKULL);
        skullTypesBuilder.put(SkullType.WITHER, WITHER_SKELETON_SKULL);
        SKULL_MATERIALS = ImmutableMap.copyOf(skullTypesBuilder);
    }

    public BlockSkull() {
        setDrops(new ItemStack(Material.SKELETON_SKULL));
    }

    /**
     * Returns the SkullType with the given ID.
     *
     * @param id the ID to look up
     * @return the skull type
     */
    public static SkullType getType(int id) {
        if (id < 0 || id >= SkullType.values().length) {
            throw new IllegalArgumentException("ID not a Skull type: " + id);
        }
        return SkullType.values()[id];
    }

    public static byte getType(SkullType type) {
        return (byte) type.ordinal();
    }

    public static boolean canRotate(Skull skull) {
        return skull.getFacing() == BlockFace.SELF;
    }

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        return BlockFace.DOWN != against; // Skulls can't be placed on bottom of block
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
                           ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        MaterialData data = state.getData();
        if (!(data instanceof Skull)) {
            warnMaterialData(Skull.class, data);
            return;
        }
        Skull skull = (Skull) data;
        skull.setFacingDirection(face);
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new SkullEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
                           GlowBlockState oldState) {
        GlowSkull skull = (GlowSkull) block.getState();
        skull.setSkullType(getType(holding.getDurability()));
        if (skull.getSkullType() == SkullType.PLAYER) {
            skull.setOwner(skull.getOwner());
        }
        MaterialData data = skull.getData();
        if (!(data instanceof Skull)) {
            warnMaterialData(Skull.class, data);
            return;
        }
        Skull skullData = (Skull) data;

        if (canRotate(skullData)) { // Can be rotated
            skull.setRotation(player.getFacing().getOppositeFace());
        }
        skull.update();

        // Wither
        for (int i = 0; i < 3; i++) {
            if (WITHER_PATTERN.matches(block.getLocation().clone(), true, i, 0)) {
                block.getWorld()
                    .spawnEntity(block.getLocation().clone().subtract(0, 2, 0), EntityType.WITHER);
                break;
            }
        }
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        GlowSkull skull = (GlowSkull) block.getState();
        Material skullMaterial = SKULL_MATERIALS.get(skull.getSkullType());
        ItemStack drop = new ItemStack(skullMaterial, 1);
        if (skull.hasOwner()) {
            SkullMeta meta = (SkullMeta) drop.getItemMeta();
            meta.setOwnerProfile(skull.getOwnerProfile());
            drop.setItemMeta(meta);
        }
        drop.setDurability((short) skull.getSkullType().ordinal());

        return Arrays.asList(drop);
    }
}
