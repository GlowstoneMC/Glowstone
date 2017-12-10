package net.glowstone.entity.objects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

import com.flowpowered.network.Message;

import net.glowstone.entity.GlowEntity;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;

public class GlowEnderPearl extends GlowEntity implements EnderPearl{

	public GlowEnderPearl(Location location) {
		super(location);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean doesBounce() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ProjectileSource getShooter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBounce(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShooter(ProjectileSource arg0) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public List<Message> createSpawnMessage() {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        return Arrays.asList(
            new SpawnObjectMessage(id, getUniqueId(), SpawnObjectMessage.BOAT, x, y, z, pitch, yaw),
            new EntityMetadataMessage(id, metadata.getEntryList()),
            // these keep the client from assigning a random velocity
            new EntityTeleportMessage(id, x, y, z, yaw, pitch),
            new EntityVelocityMessage(id, getVelocity())
        );
    }

}
