package net.glowstone.mixin.block;

import net.glowstone.block.itemtype.ItemType;
import org.bukkit.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ItemType.class, remap = false)
public abstract class MixinItemType implements org.spongepowered.api.item.ItemType {

    @Shadow
    public abstract Material getMaterial();

    @Override
    public String getName() {
        return "minecraft:" + getMaterial().name().toLowerCase();
    }

    @Override
    public String getId() {
        return "minecraft:" + getMaterial().name().toLowerCase();
    }
}
