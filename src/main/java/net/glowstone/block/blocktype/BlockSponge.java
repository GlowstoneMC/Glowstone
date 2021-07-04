package net.glowstone.block.blocktype;

import com.google.common.collect.Sets;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.TaxicabBlockIterator;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sponge;
import org.bukkit.material.types.SpongeType;
import org.bukkit.util.Vector;

import java.util.Set;

public class BlockSponge extends BlockType {

    private static final Set<Material> WATER_MATERIALS = Sets
        .immutableEnumSet(Material.WATER, Material.STATIONARY_WATER);

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        // TODO: Move this to a new method when physics works and run this on neighbour change too.

        MaterialData data = holding.getData();
        if (!(data instanceof Sponge)) {
            warnMaterialData(Sponge.class, data);
            return;
        }
        Sponge sponge = (Sponge) data;

        if (sponge.getType() == SpongeType.NORMAL) {
            GlowBlock block = state.getBlock();

            TaxicabBlockIterator iterator = new TaxicabBlockIterator(block);
            iterator.setMaxDistance(7);
            iterator.setMaxBlocks(66);
            iterator.setPredicate(x -> WATER_MATERIALS.contains(x.getType()));

            if (iterator.hasNext()) {
                sponge = sponge.clone();
                sponge.setType(SpongeType.WET);
                do {
                    iterator.next().setType(Material.AIR);
                } while (iterator.hasNext());
            }
        }

        state.setType(Material.SPONGE);
        state.setData(sponge);
    }
}
