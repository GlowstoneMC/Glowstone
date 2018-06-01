package net.glowstone.entity.passive;

import com.google.common.collect.Sets;
import java.util.Set;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class GlowCow extends GlowAnimal implements Cow {

    private static final Set<Material> BREEDING_FOODS = Sets.immutableEnumSet(Material.WHEAT);

    public GlowCow(Location location) {
        super(location, EntityType.COW, 10);
        setSize(0.9F, 1.3F);
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        super.entityInteract(player, message);
        if (message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode()
                .equals(GameMode.SPECTATOR)) {
                return false;
            }

            if (!isAdult()) {
                return false;
            }
            ItemStack hand = InventoryUtil
                .itemOrEmpty(player.getInventory().getItem(message.getHandSlot()));

            if (!hand.getType().equals(Material.BUCKET)) {
                return false;
            }

            player.getInventory().consumeItemInHand(message.getHandSlot());

            if (player.getInventory().firstEmpty() == -1) {
                GlowItem item = player.getWorld()
                    .dropItem(player.getLocation().clone().add(0, 1, 0),
                        new ItemStack(Material.MILK_BUCKET, 1));
                item.setVelocity(getLocation().add(0, -1, 0).clone().toVector()
                    .subtract(player.getLocation().clone().add(0, 1, 0).toVector()).multiply(0.3));
            } else {
                player.getInventory().addItem(new ItemStack(Material.MILK_BUCKET, 1));
            }
        }
        return true;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_COW_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_COW_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_COW_AMBIENT;
    }

    @Override
    public Set<Material> getBreedingFoods() {
        return BREEDING_FOODS;
    }
}
