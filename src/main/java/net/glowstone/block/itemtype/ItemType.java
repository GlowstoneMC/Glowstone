package net.glowstone.block.itemtype;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Base class for specific types of items.
 */
public class ItemType {

    private List<Material> materials;

    /**
     * The type of block to place when the item is used.
     *
     * @return the type of block to place
     */
    @Getter
    private BlockType placeAs;

    /**
     * The maximum stack size of the item.
     *
     * @return The maximum stack size.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private int maxStackSize = -1;


    /**
     * Get the Material assigned to this ItemType.
     *
     * @return The corresponding Material.
     * @deprecated ItemTypes can be assigned to multiple materials. Use getMaterials.
     */
    @Deprecated
    public final Material getMaterial() {
        return materials.get(0);
    }

    /**
     * Assign a Material to this ItemType (for internal use only).
     *
     * @param material The internal material for this item.
     */
    public final void setMaterial(Material material) {
        Preconditions.checkNotNull(material);
        if (materials == null) {
            materials = new ArrayList<>();
        }
        if (maxStackSize != -1 && maxStackSize != material.getMaxStackSize()) {
            throw new IllegalStateException("Material is not compatible with " + this);
        }
        materials.add(material);
        maxStackSize = material.getMaxStackSize();
    }

    /**
     * Get the Materials assigned to this ItemType.
     *
     * @return The Materials.
     */
    public final ImmutableList<Material> getMaterials() {
        return ImmutableList.copyOf(materials);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Setters for subclass use

    /**
     * Set this item to act like the given block type when being placed.
     *
     * @param placeAs The material to place as.
     */
    protected final void setPlaceAs(Material placeAs) {
        if (placeAs == null) {
            this.placeAs = null;
        } else {
            this.placeAs = ItemTable.instance().getBlock(placeAs);
            if (this.placeAs == null) {
                throw new IllegalArgumentException("Material " + placeAs + " is not a valid block");
            }
        }
    }

    /**
     * Set this item to act like the given block type when being placed.
     *
     * @param placeAs The block to place as.
     */
    protected final void setPlaceAs(BlockType placeAs) {
        // Cannot be Lombokified because of the overload
        this.placeAs = placeAs;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Actions

    /**
     * Called when a player right-clicks in midair while holding this item. Also called by default
     * if rightClickBlock is not overridden.
     *
     * @param player  The player
     * @param holding The ItemStack the player was holding
     */
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        // nothing by default
    }

    /**
     * Get the context this item can be used in.
     *
     * @return context of the item, default is {{@link Context#BLOCK}}
     */
    public Context getContext() {
        return Context.BLOCK;
    }

    /**
     * Called when a player right-clicks on a block while holding this item.
     *
     * @param player     The player
     * @param target     The block the player right-clicked
     * @param face       The face on which the click occurred
     * @param holding    The ItemStack the player was holding
     * @param clickedLoc The coordinates at which the click occurred
     * @param hand       The hand slot of this item
     */
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
                                ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        if (placeAs != null) {
            if (placeAs.getContext().isBlockApplicable()) {
                placeAs.rightClickBlock(player, target, face, holding, clickedLoc, hand);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Utility stuff

    @Override
    public final String toString() {
        return getClass().getSimpleName()
            + "{" + Optional.ofNullable(materials)
                            .orElseGet(Collections::emptyList)
                            .stream().map(Object::toString)
                            .collect(Collectors.joining(", ")) + "}";
    }

    /**
     * Context of the Items interaction.
     */
    @AllArgsConstructor
    public enum Context {
        /**
         * The item can only be used when clicking in the air.
         */
        AIR(true, false),
        /**
         * The item can only be used when clicking against a block.
         */
        BLOCK(false, true),
        /**
         * The item can be used on any click.
         */
        ANY(true, true);

        @Getter
        private boolean airApplicable;
        @Getter
        private boolean blockApplicable;
    }
}
