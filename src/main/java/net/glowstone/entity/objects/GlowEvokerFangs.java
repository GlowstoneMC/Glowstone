package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.GlowEntity;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;

import java.util.LinkedList;
import java.util.List;

public class GlowEvokerFangs extends GlowEntity implements EvokerFangs {

    @Getter
    @Setter
    private LivingEntity owner;

    public GlowEvokerFangs(Location location) {
        super(location);
        setBoundingBox(0.5, 0.8);
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> result = new LinkedList<>();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);
        result.add(new SpawnObjectMessage(
                entityId, this.getUniqueId(), 79, x, y, z, pitch, yaw, 0, 0, 0, 0));
        return result;
    }

    @Override
    public EntityType getType() {
        return EntityType.EVOKER_FANGS;
    }
}
