package net.glowstone.entity.passive;

import com.flowpowered.networking.Message;
import net.glowstone.entity.GlowAmbient;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;
import net.glowstone.net.message.play.entity.SpawnMobMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;

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

        // spawn mob
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);
        result.add(new SpawnMobMessage(id, getType().getTypeId(), x, y, z, yaw, pitch, pitch, 0, 0, 0, metadata.getEntryList()));

        // head facing
        result.add(new EntityHeadRotationMessage(id, yaw));
        return result;
    }

    @Override
    public boolean isAwake() {
        return metadata.getByte(MetadataIndex.BAT_HANGING) == 1;
    }

    @Override
    public void setAwake(boolean isAwake) {
        metadata.set(MetadataIndex.BAT_HANGING, (byte) (isAwake ? 1 : 0));
    }

    @Override
    public EntityType getType() {
        return EntityType.BAT;
    }
}
