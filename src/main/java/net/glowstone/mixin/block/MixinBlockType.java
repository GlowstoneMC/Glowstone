package net.glowstone.mixin.block;

import net.glowstone.block.blocktype.BlockType;
import net.glowstone.interfaces.block.IBlockType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collection;
import java.util.Optional;

@Mixin(BlockType.class)
public abstract class MixinBlockType implements org.spongepowered.api.block.BlockType, IBlockType {

    @Override
    public boolean getTickRandomly() {
        return canTickRandomly();
    }

    @Override
    public void setTickRandomly(boolean b) {

    }

    @Override
    public Collection<BlockTrait<?>> getTraits() {
        return null;
    }

    @Override
    public Optional<BlockTrait<?>> getTrait(String s) {
        return null;
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
    public BlockState getDefaultState() {
        return null;
    }

    @Override
    public Optional<ItemType> getItem() {
        Optional o = Optional.of(this);
        return o;
    }

    @Override
    public Translation getTranslation() {
        return null;
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Class<T> aClass) {
        return null;
    }

    @Override
    public Collection<Property<?, ?>> getApplicableProperties() {
        return null;
    }
}
