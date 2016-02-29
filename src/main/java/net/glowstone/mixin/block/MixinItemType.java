package net.glowstone.mixin.block;

import net.glowstone.block.ItemTable;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.interfaces.block.IItemType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(ItemType.class)
public abstract class MixinItemType implements org.spongepowered.api.item.ItemType, IItemType {

    @Shadow(remap = false)
    private net.glowstone.block.blocktype.BlockType placeAs;

    private BlockType getPlaceAs() {
        if (placeAs != null) {
            return (BlockType) placeAs;
        } else {
            return (BlockType) ItemTable.instance().getBlock(getMaterial());
        }
    }

    @Override
    public int getMaxStackQuantity() {
        return getMaxStackSize();
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getDefaultProperty(Class<T> aClass) {
        return null;
    }

    @Override
    public Optional<BlockType> getBlock() {
        return Optional.ofNullable(getPlaceAs());
    }

    @Override
    public String getName() {
        return "minecraft:" + getMaterial().name().toLowerCase();
    }

    @Override
    public String getId() {
        return "minecraft:" + getMaterial().name().toLowerCase();
    }

    @Override
    public Translation getTranslation() {
        return null;
    }
}
