package net.glowstone.block.entity.state;

import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BeaconEntity;
import org.bukkit.block.Beacon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GlowBeacon extends GlowContainer implements Beacon {

    @Getter
    private PotionEffect primaryEffect;
    @Getter
    private PotionEffect secondaryEffect;

    /**
     * Creates an entity for the given beacon block.
     *
     * @param block the block this beacon occupies
     */
    public GlowBeacon(GlowBlock block) {
        super(block);
        if (getBlockEntity().getPrimaryId() > 0) {
            setPrimaryEffect(PotionEffectType.getById(getBlockEntity().getPrimaryId()));
        }
        if (getBlockEntity().getSecondaryId() > 0) {
            setSecondaryEffect(PotionEffectType.getById(getBlockEntity().getSecondaryId()));
        }
    }

    private BeaconEntity getBlockEntity() {
        return (BeaconEntity) getBlock().getBlockEntity();
    }

    @Override
    public Collection<LivingEntity> getEntitiesInRange() {
        return Collections.emptyList();
    }

    @Override
    public int getTier() {
        return getBlockEntity().getLevels();
    }

    @Override
    public void setPrimaryEffect(PotionEffectType primary) {
        this.primaryEffect = new PotionEffect(primary, 7, getTier(), true);
        getBlockEntity().setPrimaryId(primary.getId());
    }

    @Override
    public void setSecondaryEffect(PotionEffectType secondary) {
        this.secondaryEffect = new PotionEffect(secondary, 7, getTier(), true);
        getBlockEntity().setSecondaryId(secondary.getId());
    }

    @Override
    public BeaconInventory getInventory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BeaconInventory getSnapshotInventory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
