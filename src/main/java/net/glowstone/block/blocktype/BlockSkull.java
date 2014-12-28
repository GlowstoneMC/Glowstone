package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TESkull;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.block.state.GlowSkull;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Skull;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;

public class BlockSkull extends BlockType {

    public BlockSkull() {
        setDrops(new ItemStack(Material.SKULL_ITEM));
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        return BlockFace.DOWN != against; // Skulls can't be placed on bottom of block
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
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
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TESkull(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding) {
        GlowSkull skull = (GlowSkull) block.getState();
        skull.setSkullType(getType(holding.getDurability()));
        if (skull.getSkullType() == SkullType.PLAYER) {
            SkullMeta meta = (SkullMeta) holding.getItemMeta();
            if (meta != null) {
                skull.setOwner(meta.getOwner());
            }
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
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        GlowSkull skull = (GlowSkull) block.getState();

        ItemStack drop = new ItemStack(Material.SKULL_ITEM, 1);
        if (skull.hasOwner()) {
            SkullMeta meta = (SkullMeta) drop.getItemMeta();
            meta.setOwner(skull.getOwner());
            drop.setItemMeta(meta);
        }
        drop.setDurability((short) skull.getSkullType().ordinal());

        return Arrays.asList(drop);
    }

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
}
