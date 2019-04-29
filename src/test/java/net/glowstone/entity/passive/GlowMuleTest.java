package net.glowstone.entity.passive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import net.glowstone.net.message.play.player.InteractEntityMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class GlowMuleTest extends GlowChestedHorseTest<GlowMule> {
    public GlowMuleTest() {
        super(GlowMule::new);
    }

    @Test
    @Override
    public void testSetAgeAdult() {
        entity.setAge(0);
        assertEquals(0, entity.getAge());
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

    @Test
    @Override
    public void testSetBreedTrueBaby() {
        entity.setBaby();
        entity.setBreed(true);
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

    @Test
    @Override
    public void testSetBreedTrueAdult() {
        entity.setAge(1);
        assertFalse(entity.canBreed());
        entity.setBreed(true);
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

    @Test
    @Override
    public void testFoodSetsLoveMode() {
        entity.setTamed(true);
        InteractEntityMessage interact = new InteractEntityMessage(1,
                InteractEntityMessage.Action.INTERACT.ordinal(), /* main hand */ 0);
        for (Material foodType : entity.getBreedingFoods()) {
            ItemStack food = new ItemStack(foodType, 1);
            inventory.setItemInMainHand(food);
            entity.entityInteract(player, interact);

            // Should not consume food
            assertEquals(1, inventory.getItemInMainHand().getAmount());

            // Should not set love mode
            assertEquals(0, entity.getInLove());
        }
    }
}
