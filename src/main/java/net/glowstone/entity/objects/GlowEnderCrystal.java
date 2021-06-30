package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import java.util.Arrays;
import java.util.List;
import net.glowstone.EventFactory;
import net.glowstone.Explosion;
import net.glowstone.entity.EntityNetworkUtil;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage.Action;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public class GlowEnderCrystal extends GlowEntity implements EnderCrystal {

    /**
     * Creates an instance at the given location.
     *
     * @param location the ender crystal's location
     */
    public GlowEnderCrystal(Location location) {
        super(location);
        setSize(2f, 2f);
        setShowingBottom(true);
        setGravity(false);
    }

    @Override
    public EntityType getType() {
        return EntityType.ENDER_CRYSTAL;
    }

    @Override
    public List<Message> createSpawnMessage() {
        return Arrays.asList(
            new SpawnObjectMessage(entityId,
                getUniqueId(), EntityNetworkUtil.getObjectId(EntityType.ENDER_CRYSTAL),
                location),
            new EntityMetadataMessage(entityId, metadata.getEntryList())
        );
    }

    @Override
    public void pulse() {
        super.pulse();

        // "While in the End, [..] will continually generate fire, [..] replacing any other block"
        if (world.getEnvironment() == Environment.THE_END) {
            Block block = location.getBlock();
            if (block.getType() != Material.FIRE) {
                block.setType(Material.FIRE);
            }
        }
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (message.getAction() != Action.ATTACK.ordinal()) {
            return false;
        }

        damage(0, this, DamageCause.BLOCK_EXPLOSION);

        return true;
    }

    @Override
    public void damage(double amount, Entity source, @NotNull DamageCause cause) {
        if (source instanceof EnderDragon) {
            return;
        }

        if (cause != DamageCause.ENTITY_EXPLOSION) {
            ExplosionPrimeEvent event = EventFactory.getInstance()
                .callEvent(new ExplosionPrimeEvent(this, Explosion.POWER_ENDER_CRYSTAL, true));

            if (!event.isCancelled()) {
                Location location = getLocation();
                double x = location.getX();
                double y = location.getY();
                double z = location.getZ();
                world.createExplosion(this, x, y, z, event.getRadius(), event.getFire(), true);
            }
        }
        remove();
    }

    @Override
    public boolean isShowingBottom() {
        return metadata.getBoolean(MetadataIndex.ENDERCRYSTAL_SHOW_BOTTOM);
    }

    @Override
    public void setShowingBottom(boolean showing) {
        metadata.set(MetadataIndex.ENDERCRYSTAL_SHOW_BOTTOM, showing);
    }

    @Override
    public Location getBeamTarget() {
        BlockVector beamTarget = metadata.getOptPosition(MetadataIndex.ENDERCRYSTAL_BEAM_TARGET);
        if (beamTarget == null) {
            return null;
        }
        return beamTarget.toLocation(getWorld());
    }

    @Override
    public void setBeamTarget(Location location) {
        if (location == null) {
            metadata.set(MetadataIndex.ENDERCRYSTAL_BEAM_TARGET, (BlockVector) null);
        } else if (!location.getWorld().equals(getWorld())) {
            throw new IllegalArgumentException(
                "Cannot set beam target location to different world");
        } else {
            metadata.set(MetadataIndex.ENDERCRYSTAL_BEAM_TARGET,
                new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
    }
}
