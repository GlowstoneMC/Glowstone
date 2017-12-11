package net.glowstone.entity.objects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.flowpowered.network.Message;

import net.glowstone.command.minecraft.PlaySoundCommand;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;

public class GlowEnderPearl extends GlowEntity implements EnderPearl{
	private ProjectileSource shooter;
	
	public GlowEnderPearl(Location location) {
		super(location);
		setDrag(0.99, false);
		setDrag(0.99, true);
		setGravityAccel(new Vector(0,-0.06,0));
	}

	@Override
	public boolean doesBounce() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ProjectileSource getShooter() {
		// TODO Auto-generated method stub
		return this.shooter;
	}

	@Override
	public void setBounce(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShooter(ProjectileSource source) {
		// TODO Auto-generated method stub
		this.shooter = source;
		((GlowPlayer) source).playSound(location, Sound.ENTITY_ENDERPEARL_THROW, 3, 1);
	}

	@Override
	protected void pulsePhysics() {
		// TODO Auto-generated method stub
		Location velLoc = location.clone().add(velocity);
		velocity.setY(airDrag * (velocity.getY() + getGravityAccel().getY()));

		velocity.setX(velocity.getX() * 0.95);
		velocity.setZ(velocity.getZ() * 0.95);

		setRawLocation(velLoc);
		
		//If the EnderPearl collides with anything except air/fluids
		if(!isTouchingMaterial(Material.AIR) && 
				!isTouchingMaterial(Material.WATER) && 
				!isTouchingMaterial(Material.LAVA) &&
				!isTouchingMaterial(Material.STATIONARY_WATER) &&
				!isTouchingMaterial(Material.STATIONARY_LAVA)) {
			System.out.println(world.getBlockTypeIdAt(location));
			((GlowPlayer) shooter).teleport(location);
			this.remove();
		}
	}

	@Override
	public List<Message> createSpawnMessage() {
		double x, y, z, yaw, pitch, speed;


		x = location.getX();
		//Add 1.5m to account for eye level
		y = location.getY() + 1.75;
		z = location.getZ();
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
