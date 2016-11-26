package net.glowstone.entity.monster;

import com.flowpowered.network.Message;
import net.glowstone.entity.GlowEntity;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GlowEvokerFangs extends GlowEntity implements EvokerFangs {

    private LivingEntity owner;

    public GlowEvokerFangs(Location location) {
        super(location);
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> result = new LinkedList<>();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);
        result.add(new SpawnObjectMessage(id, UUID.randomUUID(), getType().getTypeId(), x, y, z, pitch, yaw, 0, 0, 0, 0));
        return result;
    }

    @Override
    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    @Override
    public EntityType getType() {
        return EntityType.EVOKER_FANGS;
    }
}
