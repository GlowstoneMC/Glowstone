package net.glowstone.entity.passive;

import com.google.common.collect.Sets;
import java.util.Set;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GlowPig extends GlowAnimal implements Pig {

    private static final Set<Material> BREEDING_FOODS = Sets.immutableEnumSet(Material.CARROT,
            Material.POTATO,
            Material.BEETROOT);

    public GlowPig(Location location) {
        super(location, EntityType.PIG, 10);
        setSize(0.9F, 0.9F);
    }

    @Override
    public boolean hasSaddle() {
        return metadata.getBoolean(MetadataIndex.PIG_SADDLE);
    }

    @Override
    public void setSaddle(boolean hasSaddle) {
        metadata.set(MetadataIndex.PIG_SADDLE, hasSaddle);
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        super.entityInteract(player, message);
        if (message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            if (!isAdult()) {
                return false;
            }
            if (!hasSaddle()) {
                ItemStack hand = InventoryUtil
                    .itemOrEmpty(player.getInventory().getItem(message.getHandSlot()));
                if (hand.getType() == Material.SADDLE) {
                    setSaddle(true);
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        player.getInventory().consumeItemInHand(message.getHandSlot());
                    }
                    return true;
                }
                return false;
            }
            return isEmpty() && setPassenger(player);
        }
        return false;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_PIG_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_PIG_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_PIG_AMBIENT;
    }

    @Override
    public void damage(double amount, Entity source, @NotNull DamageCause cause) {
        if (!DamageCause.LIGHTNING.equals(cause)) {
            super.damage(amount, source, cause);
            return;
        }

        PigZombie pigZombie = world.spawn(this.location, PigZombie.class);
        pigZombie.damage(amount, source, cause);
        remove();
    }

    @Override
    public Set<Material> getBreedingFoods() {
        return BREEDING_FOODS;
    }
}
