package net.glowstone.entity.passive;

import com.flowpowered.network.Message;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.HorseInventory;

public abstract class GlowAbstractHorse extends GlowTameable implements AbstractHorse {

    @Getter
    @Setter
    private int domestication;
    @Getter
    @Setter
    private int maxDomestication;
    @Getter
    @Setter
    private double jumpStrength;
    @Getter
    @Setter
    private boolean tamed;

    public GlowAbstractHorse(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
        setSize(1.3964f, 1.6f);
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> messages = super.createSpawnMessage();
        MetadataMap map = new MetadataMap(GlowHorse.class);
        map.set(MetadataIndex.ABSTRACT_HORSE_FLAGS, getHorseFlags());
        messages.add(new EntityMetadataMessage(entityId, map.getEntryList()));
        return messages;
    }

    @Override
    public Horse.Variant getVariant() {
        // Field has been removed in 1.11
        return null;
    }

    @Override
    public void setVariant(Horse.Variant variant) {
        // Field has been removed in 1.11
    }

    private int getHorseFlags() {
        int value = 0;
        if (isTamed()) {
            value |= 0x02;
        }
        if (this instanceof GlowHorse) {
            GlowHorse horse = (GlowHorse) this;
            if (getInventory() != null && ((HorseInventory) getInventory()).getSaddle() != null) {
                value |= 0x04;
            }
            if (horse.hasReproduced()) {
                value |= 0x10;
            }
            if (horse.isEatingHay()) {
                value |= 0x20;
            }
        }
        if (this instanceof ChestedHorse) {
            ChestedHorse horse = (ChestedHorse) this;
            if (horse.isCarryingChest()) {
                value |= 0x08;
            }
        }
        return value;
    }
}
