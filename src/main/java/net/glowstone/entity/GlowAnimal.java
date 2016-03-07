package net.glowstone.entity;

import net.glowstone.net.message.play.player.InteractEntityMessage;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnMeta;

/**
 * Represents an Animal, such as a Cow
 */
public class GlowAnimal extends GlowAgeable implements Animals {

    /**
     * Creates a new ageable animal.
     *
     * @param location The location of the animal.
     * @param type     The type of animal.
     * @param maxHealth The max health of this animal.
     */
    public GlowAnimal(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        super.entityInteract(player, message);
        ItemStack item = player.getItemInHand();

        if (player.getGameMode() == GameMode.SPECTATOR)
            return false;

        if (item != null && item.getType() == Material.MONSTER_EGG && item.hasItemMeta()) {
            SpawnMeta meta = (SpawnMeta) item.getItemMeta();
            if (meta.hasEntityType() && meta.getEntityType() == this.getType()) {
                Class<? extends GlowEntity> spawn = EntityRegistry.getEntity(getType().getTypeId());
                GlowAnimal animal = (GlowAnimal) getWorld().spawn(getLocation(), spawn, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
                animal.setBaby();

                if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                    // Consume the egg
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().clear(player.getInventory().getHeldItemSlot());
                    }
                }
                return true;
            }
        }
        return false;
    }
}
