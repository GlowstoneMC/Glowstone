package net.glowstone.mixin.event.entity;

import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.List;

@Mixin(value = EntitySpawnEvent.class, remap = false)
public abstract class MixinEntitySpawnEvent extends EntityEvent implements org.spongepowered.api.event.entity.SpawnEntityEvent {

    public MixinEntitySpawnEvent(org.bukkit.entity.Entity what) {
        super(what);
    }

    @Override
    public World getTargetWorld() {
        return (World) getEntity().getWorld();
    }

    @Override
    public List<EntitySnapshot> getEntitySnapshots() {
        return Collections.emptyList();
    }

    @Override
    public List<Entity> getEntities() {
        return Collections.emptyList();
    }
}
