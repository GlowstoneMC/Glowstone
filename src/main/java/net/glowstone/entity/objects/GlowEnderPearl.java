package net.glowstone.entity.objects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

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
	protected void pulsePhysics() {
		// TODO Auto-generated method stub
		Location velLoc = location.clone().add(velocity);
		velocity.setY(airDrag * (velocity.getY() + getGravityAccel().getY()));

		velocity.setX(velocity.getX() * 0.95);
		velocity.setZ(velocity.getZ() * 0.95);

		setRawLocation(velLoc);
	}

	@Override
	public List<Message> createSpawnMessage() {
		double x, y, z, yaw, pitch, speed;
		this.airDrag = 0.99;
		this.gravityAccel = new Vector(0,-0.06,0);


		x = location.getX();
		//Add 1.5m to account for eye level
		y = location.getY() + 1.5;
		z = location.getZ();
		//Correct Notchian pich and yaw & convert to radians
		yaw  = location.getYaw();
		pitch = location.getPitch();
		Vector vel = location.getDirection().multiply(2);
		setVelocity(vel);

		return Arrays.asList(
				new SpawnObjectMessage(id, getUniqueId(), SpawnObjectMessage.THROWN_ENDERPEARL, x, y, z, (int) yaw, (int) pitch),
				new EntityMetadataMessage(id, metadata.getEntryList()),
				// these keep the client from assigning a random velocity
				new EntityTeleportMessage(id, x, y, z, (int)yaw, (int)pitch),
				new EntityVelocityMessage(id, getVelocity())
				);
	}

}
