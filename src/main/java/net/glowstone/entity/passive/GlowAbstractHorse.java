package net.glowstone.entity.passive;

import com.flowpowered.network.Message;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.TickUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

public abstract class GlowAbstractHorse extends GlowTameable implements AbstractHorse {

    private static final Set<Material> BREEDING_FOODS = Sets.immutableEnumSet(Material.GOLDEN_APPLE,
            Material.GOLDEN_CARROT);

    private static final Map<Material, Integer> GROWING_FOODS = ImmutableMap
            .<Material, Integer>builder()
            .put(Material.SUGAR, TickUtil.secondsToTicks(30))
            .put(Material.WHEAT, TickUtil.secondsToTicks(20))
            .put(Material.APPLE, TickUtil.minutesToTicks(1))
            .put(Material.GOLDEN_CARROT, TickUtil.minutesToTicks(1))
            .put(Material.GOLDEN_APPLE, TickUtil.minutesToTicks(4))
            .put(Material.HAY_BLOCK, TickUtil.minutesToTicks(3))
            .build();

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

    @Override
    public boolean canBreed() {
        return super.canBreed() && isTamed();
    }

    private int getHorseFlags() {
        int value = 0;
        if (isTamed()) {
            value |= 0x02;
        }
        if (this instanceof GlowHorse) {
            GlowHorse horse = (GlowHorse) this;
            if (getInventory() != null && !InventoryUtil.isEmpty(getInventory().getSaddle())) {
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

    @Override
    public Set<Material> getBreedingFoods() {
        return BREEDING_FOODS;
    }

    @Override
    protected int computeGrowthAmount(Material material) {
        // We need to be a baby and only tamed horses can be fed with hay block
        if (canGrow() && (Material.HAY_BLOCK != material || isTamed())) {
            return Math.min(GROWING_FOODS.getOrDefault(material, 0), Math.abs(getAge()));
        }
        return 0;
    }
}
