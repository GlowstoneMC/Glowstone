
package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BeaconEntity;
import org.bukkit.block.Beacon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Collections;

public class GlowBeacon extends GlowLootableBlock implements Beacon {

    private PotionEffect primaryEffect;
    private PotionEffect secondaryEffect;

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
    public PotionEffect getPrimaryEffect() {
        return primaryEffect;
    }

    @Override
    public void setPrimaryEffect(PotionEffectType primary) {
        this.primaryEffect = new PotionEffect(primary, 7, getTier(), true);
        getBlockEntity().setPrimaryId(primary.getId());
    }

    @Override
    public PotionEffect getSecondaryEffect() {
        return secondaryEffect;
    }

    @Override
    public void setSecondaryEffect(PotionEffectType secondary) {
        this.secondaryEffect = new PotionEffect(secondary, 7, getTier(), true);
        getBlockEntity().setSecondaryId(secondary.getId());
    }

    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
