package net.glowstone.entity.passive;

import com.flowpowered.network.Message;
import net.glowstone.entity.GlowAmbient;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.EntityHeadRotationPacket;
import net.glowstone.net.message.play.entity.SpawnMobPacket;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GlowBat extends GlowAmbient implements Bat {

    public GlowBat(Location location) {
        super(location, 6);
        setSize(0.5F, 0.9F);
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> result = new LinkedList<>();

        // spawn mob
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);
        result.add(new SpawnMobPacket(id, UUID.randomUUID(), getType().getTypeId(), x, y, z, yaw, pitch, pitch, 0, 0, 0, metadata.getEntryList())); //TODO 1.9 - Real UUID

        // head facing
        result.add(new EntityHeadRotationPacket(id, yaw));
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

    @Override
    public void setGlowing(boolean b) {

    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public void setInvulnerable(boolean b) {

    }

    @Override
    public boolean isInvulnerable() {
        return false;
    }

    @Override
    public Location getOrigin() {
        return null;
    }

    @Override
    public AttributeInstance getAttribute(Attribute attribute) {
        return null;
    }

    @Override
    public boolean isGliding() {
        return false;
    }

    @Override
    public void setGliding(boolean b) {

    }

    @Override
    public void setAI(boolean b) {

    }

    @Override
    public boolean hasAI() {
        return false;
    }

    @Override
    public void setCollidable(boolean b) {

    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public int getArrowsStuck() {
        return 0;
    }

    @Override
    public void setArrowsStuck(int i) {

    }
}
