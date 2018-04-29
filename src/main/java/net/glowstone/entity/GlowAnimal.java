package net.glowstone.entity;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import net.glowstone.entity.ai.EntityDirector;
import net.glowstone.entity.ai.MobState;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an Animal, such as a Cow.
 */
public class GlowAnimal extends GlowAgeable implements Animals {

    private static final Set<Material> DEFAULT_BREEDING_FOODS =
            Sets.immutableEnumSet(EnumSet.noneOf(Material.class));

    /**
     * Creates a new ageable animal.
     *
     * @param location The location of the animal.
     * @param type The type of animal.
     * @param maxHealth The max health of this animal.
     */
    public GlowAnimal(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
        if (type != null) {
            EntityDirector.registerEntityMobState(type, MobState.IDLE, "look_around");
            EntityDirector.registerEntityMobState(type, MobState.IDLE, "look_player");
        }
        setState(MobState.IDLE);
    }

    @Override
    protected int getAmbientDelay() {
        return 120;
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (!super.entityInteract(player, message)
                && message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            ItemStack item = InventoryUtil
                    .itemOrEmpty(player.getInventory().getItem(message.getHandSlot()));

            if (player.getGameMode().equals(GameMode.SPECTATOR)
                    || InventoryUtil.isEmpty(item)) {
                return false;
            }

            if (GameMode.CREATIVE != player.getGameMode()
                    && getBreedingFoods().contains(item.getType())) {
                // TODO set love mode if possible and spawn particles
                // TODO heal
                // TODO only consume the item if the animal is healed or something else
                player.getInventory().consumeItemInMainHand();
                return true;
            }
        }

        return false;
    }

    /**
     * Returns an immutable set containing the breeding foods for the current animal.
     * @return an immutable set containing Material
     */
    public Set<Material> getBreedingFoods() {
        return DEFAULT_BREEDING_FOODS;
    }

    @Override
    protected int computeGrowthAmount(Material material) {
        if (!isAdult() && getBreedingFoods().contains(material)) {
            return Math.abs(getAge() / 10);
        }

        return 0;
    }
}
