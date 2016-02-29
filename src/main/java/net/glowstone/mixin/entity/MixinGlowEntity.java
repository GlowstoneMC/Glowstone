package net.glowstone.mixin.entity;

import com.flowpowered.math.vector.Vector3d;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.api.util.persistence.InvalidDataException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.*;

@Mixin(GlowEntity.class)
public abstract class MixinGlowEntity implements Entity {

    @Shadow(remap = false)
    public abstract GlowWorld shadow$getWorld();

    @Shadow(remap = false)
    public abstract org.bukkit.Location shadow$getLocation();

    @Override
    public EntityType getType() {
        return null;
    }

    @Override
    public World getWorld() {
        return (World) (Object) shadow$getWorld();
    }

    @Override
    public EntitySnapshot createSnapshot() {
        return null;
    }

    @Override
    public Random getRandom() {
        return null;
    }

    @Override
    public Location<World> getLocation() {
        return null;
    }
}
