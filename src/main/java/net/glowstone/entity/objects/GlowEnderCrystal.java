package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import java.util.Arrays;
import java.util.List;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BlockVector;

public class GlowEnderCrystal extends GlowEntity implements EnderCrystal {

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
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        return Arrays.asList(
            new SpawnObjectMessage(id, getUniqueId(), SpawnObjectMessage.ENDER_CRYSTAL, x, y, z, pitch, yaw),
            new EntityMetadataMessage(id, metadata.getEntryList())
        );
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
            throw new IllegalArgumentException("Cannot set beam target location to different world");
        } else {
            metadata.set(MetadataIndex.ENDERCRYSTAL_BEAM_TARGET, new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
    }
}
