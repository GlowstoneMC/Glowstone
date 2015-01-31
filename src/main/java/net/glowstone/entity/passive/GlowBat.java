package net.glowstone.entity.passive;

import com.flowpowered.networking.Message;
import net.glowstone.entity.GlowAmbient;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.SpawnMobMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;

import java.util.LinkedList;
import java.util.List;

public class GlowBat extends GlowAmbient implements Bat {

    private boolean isAwake;

    public GlowBat(Location location) {
        super(location);
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
        MetadataMap map = new MetadataMap(GlowBat.class);
        map.set(MetadataIndex.BAT_HANGING, (byte) (this.isAwake ? 1 : 0));
        result.add(new EntityMetadataMessage(id, map.getEntryList()));
        return result;
    }

    @Override
    public boolean isAwake() {
        return isAwake;
    }

    @Override
    public void setAwake(boolean isAwake) {
        this.isAwake = isAwake;
    }

    @Override
    public EntityType getType() {
        return EntityType.BAT;
    }
}
