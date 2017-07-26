package net.glowstone.block.itemtype;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparingInt;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowPainting;
import org.bukkit.Art;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemPainting extends ItemType {

    // Art, sorted by size descending
    private static final List<Art> SORTED_ART;

    static {
        SORTED_ART = Arrays.stream(Art.values())
            .sorted(
                reverseOrder(
                    comparingInt(Art::getBlockHeight)
                    .thenComparingInt(Art::getBlockWidth)
                )
            )
            .collect(Collectors.toList());
    }

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face, ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        GlowPainting painting = new GlowPainting(target.getRelative(face).getLocation(), face);

        for (Art art : SORTED_ART) {
            painting.setArtInternal(art);
            if (!painting.isObstructed()) {
                break;
            }
        }
    }
}
