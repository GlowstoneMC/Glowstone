package net.glowstone.block.entity;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.google.common.collect.Sets;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.state.GlowBeacon;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BeaconEntity extends BlockEntity {

    private static final Set<Material> BEACON_BASE = Sets.newHashSet(
            Material.EMERALD_BLOCK, Material.GOLD_BLOCK,
            Material.DIAMOND_BLOCK, Material.IRON_BLOCK
    );
    private static final Set<Material> TRANSPARENT_BLOCKS = Sets.newHashSet(
            Material.AIR, Material.GLASS, Material.THIN_GLASS,
            Material.STAINED_GLASS, Material.STAINED_GLASS_PANE
    );

    private String lock = null; // todo: support item locks
    @Getter
    @Setter
    private int levels;
    @Getter
    @Setter
    private int primaryEffectId;
    @Getter
    @Setter
    private int secondaryEffectId;

    public BeaconEntity(GlowBlock block) {
        super(block);
        setSaveId("minecraft:beacon");
    }

    @Override
    public GlowBeacon getState() {
        return new GlowBeacon(block);
    }

    @Override
    public void update(GlowPlayer player) {
        player.setWindowProperty(InventoryView.Property.LEVELS, levels);
        player.setWindowProperty(InventoryView.Property.PRIMARY_EFFECT, primaryEffectId);
        player.setWindowProperty(InventoryView.Property.SECONDARY_EFFECT, secondaryEffectId);
    }

    public void onPulse() {
        if (canBeEnabled()) {
            calculateLevels();
            applyPotionEffects();
        }
    }

    private boolean canBeEnabled() {
        World world = block.getWorld();
        for (int y = block.getY() + 1; y < world.getMaxHeight(); y++) {
            if (!TRANSPARENT_BLOCKS.contains(world.getBlockAt(block.getX(), y, block.getZ()).getType())) {
                return false;
            }
        }
        return true;
    }

    private void calculateLevels() {
        int beaconX = block.getX();
        int beaconY = block.getY();
        int beaconZ = block.getZ();
        for (int layer = 1; layer <= 4; this.levels = layer++) {
            int layerY = beaconY - layer;
            if (layerY < 0) {
                break;
            }
            boolean isLayerComplete = true;
            for (int x = beaconX - layer; x <= beaconX + layer && isLayerComplete; ++x) {
                for (int z = beaconZ - layer; z <= beaconZ + layer; ++z) {
                    if (!BEACON_BASE.contains(getBlock().getWorld().getBlockAt(x, layerY, z).getType())) {
                        isLayerComplete = false;
                        break;
                    }
                }
            }
            if (!isLayerComplete) {
                break;
            }
        }
    }

    private void applyPotionEffects() {
        Beacon beacon = (Beacon) getBlock().getState();

        beacon.getEntitiesInRange().forEach(livingEntity -> {
            for (BeaconEffectPriority priority : BeaconEffectPriority.values()) {
                PotionEffect effect = this.getEffect(priority);
                if (effect == null) {
                    continue;
                }
                BeaconEffectEvent event = new BeaconEffectEvent(block, effect, (Player) livingEntity, priority == BeaconEffectPriority.PRIMARY);
                if (!EventFactory.getInstance().callEvent(event).isCancelled()) {
                    livingEntity.addPotionEffect(event.getEffect(), true);
                }
            }
        });
    }

    public PotionEffect getEffect(BeaconEffectPriority priority) {
        PotionEffectType type = PotionEffectType.getById(priority == BeaconEffectPriority.PRIMARY ? primaryEffectId : secondaryEffectId);
        if (type == null) {
            return null;
        }
        int effectDuration = (9 + levels * 2) * 20;
        int effectAmplifier = levels >= 4 && primaryEffectId == secondaryEffectId ? 1 : 0;
        return new PotionEffect(type, effectDuration, effectAmplifier, true, true);
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        tag.readString("Lock", lock -> this.lock = lock);
        tag.readInt("Levels", this::setLevels);
        tag.readInt("Primary", this::setPrimaryEffectId);
        tag.readInt("Secondary", this::setSecondaryEffectId);
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        if (lock != null) {
            tag.putString("Lock", lock);
        }
        tag.putInt("Levels", levels);
        tag.putInt("Primary", primaryEffectId);
        tag.putInt("Secondary", secondaryEffectId);
    }

    public enum BeaconEffectPriority {
        PRIMARY,
        SECONDARY
    }
}
