package net.glowstone.entity.passive;

import com.flowpowered.network.Message;
import net.glowstone.entity.EntityNetworkUtil;
import net.glowstone.entity.GlowAmbient;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.SpawnEntityMessage;
import net.glowstone.util.Position;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class GlowBat extends GlowAmbient implements Bat {

    public GlowBat(Location location) {
        super(location, 6);
        setSize(0.5F, 0.9F);
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> result = new LinkedList<>();

        result.add(new SpawnEntityMessage(
                entityId, getUniqueId(), EntityNetworkUtil.getMobId(EntityType.BAT),
                location));
        result.add(new EntityMetadataMessage(entityId, metadata.getEntryList()));

        // head facing
        result.add(new EntityHeadRotationMessage(entityId, Position.getIntYaw(location)));
        return result;
    }

    @Override
    public boolean isAwake() {
        return !metadata.getBit(MetadataIndex.BAT_FLAGS, MetadataIndex.BatFlags.IS_HANGING);
    }

    @Override
    public void setAwake(boolean isAwake) {
        metadata.setBit(MetadataIndex.BAT_FLAGS, MetadataIndex.BatFlags.IS_HANGING, !isAwake);
    }

    @Override
    public @Nullable Location getTargetLocation() {
        return null;
    }

    @Override
    public void setTargetLocation(@Nullable Location location) {

    }

    @Override
    public EntityType getType() {
        return EntityType.BAT;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_BAT_HURT;
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_BAT_DEATH;
    }

    @Override
    public Sound getAmbientSound() {
        return Sound.ENTITY_BAT_AMBIENT;
    }

    @Override
    public LivingEntity getTarget() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setTarget(LivingEntity target) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public @NotNull TriState getFrictionState() {
        return null;
    }

    @Override
    public void setFrictionState(@NotNull TriState state) {

    }

    @Override
    public <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> projectile, @Nullable Vector velocity, @Nullable Consumer<T> function) {
        return null;
    }
}
