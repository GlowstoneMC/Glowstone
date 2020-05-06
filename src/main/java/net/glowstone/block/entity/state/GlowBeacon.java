package net.glowstone.block.entity.state;

import java.util.Collection;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BeaconEntity;
import net.glowstone.inventory.GlowBeaconInventory;
import org.bukkit.block.Beacon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GlowBeacon extends GlowContainer implements Beacon {

    private static final int RADIUS_MULTIPLIER = 10;

    /**
     * Creates an entity for the given beacon block.
     *
     * @param block the block this beacon occupies
     */
    public GlowBeacon(GlowBlock block) {
        super(block);
    }

    private BeaconEntity getBlockEntity() {
        return (BeaconEntity) getBlock().getBlockEntity();
    }

    @Override
    public Collection<LivingEntity> getEntitiesInRange() {
        return getWorld().getNearbyEntitiesByType(Player.class, getLocation(), getTier() * RADIUS_MULTIPLIER, getWorld().getMaxHeight());
    }

    @Override
    public int getTier() {
        return getBlockEntity().getLevels();
    }

    @Override
    public void setPrimaryEffect(PotionEffectType primary) {
        getBlockEntity().setPrimaryEffectId(primary.getId());
    }

    @Override
    public void setSecondaryEffect(PotionEffectType secondary) {
        getBlockEntity().setSecondaryEffectId(secondary.getId());
    }

    @Override
    public PotionEffect getPrimaryEffect() {
        return getBlockEntity().getEffect(BeaconEntity.BeaconEffectPriority.PRIMARY);
    }

    @Override
    public PotionEffect getSecondaryEffect() {
        return getBlockEntity().getEffect(BeaconEntity.BeaconEffectPriority.SECONDARY);
    }

    @Override
    public BeaconInventory getInventory() {
        return new GlowBeaconInventory(this);
    }

    @Override
    public BeaconInventory getSnapshotInventory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
