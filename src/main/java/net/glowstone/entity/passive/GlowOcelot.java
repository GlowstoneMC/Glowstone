package net.glowstone.entity.passive;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;

import java.util.List;

public class GlowOcelot extends GlowTameable implements Ocelot {

    private Type catType;

    public GlowOcelot(Location location) {
        super(location, EntityType.OCELOT);
        setCatType(Type.WILD_OCELOT);
        setMaxHealthAndHealth(10);
    }

    @Override
    public List<Message> createSpawnMessage() {
        metadata.set(MetadataIndex.OCELOT_TYPE, catType.getId());
        return super.createSpawnMessage();
    }

    @Override
    public Type getCatType() {
        return catType;
    }

    @Override
    public void setCatType(Type type) {
        metadata.set(MetadataIndex.OCELOT_TYPE, catType.getId());
        this.catType = type;
    }

}
