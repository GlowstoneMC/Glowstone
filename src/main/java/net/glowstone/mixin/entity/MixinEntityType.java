package net.glowstone.mixin.entity;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = org.bukkit.entity.EntityType.class, remap = false)
public abstract class MixinEntityType implements EntityType {

    @Shadow
    public abstract String getName();

    @Shadow
    private Class<? extends org.bukkit.entity.Entity> clazz;

    @Override
    public Class<? extends Entity> getEntityClass() {
        return (Class<? extends Entity>) clazz;
    }

    @Override
    public String getId() {
        return "minecraft:" + getName().toLowerCase();
    }

    @Override
    public Translation getTranslation() {
        return null;
    }
}
