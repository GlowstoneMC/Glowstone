package net.glowstone.entity.passive;

import com.flowpowered.network.Message;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.GlowProjectile;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;

public class GlowFishingHook extends GlowProjectile implements FishHook {
    private int lived;
    private int lifeTime;

    public GlowFishingHook(Location location) {
        super(location);
        lifeTime = ThreadLocalRandom.current().nextInt(0, 46);
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> spawnMessage = super.createSpawnMessage();

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        int intPitch = Position.getIntPitch(location);
        int intHeadYaw = Position.getIntHeadYaw(location.getYaw());

        spawnMessage.add(new SpawnObjectMessage(this.getEntityId(), this.getUniqueId(), SpawnObjectMessage.FISHING_HOOK, x, y, z, intPitch, intHeadYaw, 0, velocity));
        return spawnMessage;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public double getBiteChance() {
        // Not supported in newer mc versions anymore
        return 0;
    }

    @Override
    public void setBiteChance(double v) throws IllegalArgumentException {
        // Not supported in newer mc versions anymore
    }

    @Override
    public void pulse() {
        super.pulse();
    }
}
