package net.glowstone.mixin.block;

import net.glowstone.block.blocktype.BlockType;
import org.bukkit.Material;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(BlockType.class)
public abstract class MixinBlockType implements org.spongepowered.api.block.BlockType {

    @Shadow(remap = false)
    public abstract boolean canTickRandomly();

    @Override
    public boolean getTickRandomly() {
        return canTickRandomly();
    }

    @Override
    public Optional<ItemType> getItem() {
        Optional o = Optional.of(this);
        return o;
    }

}
