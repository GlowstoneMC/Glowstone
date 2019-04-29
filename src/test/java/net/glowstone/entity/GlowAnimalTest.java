package net.glowstone.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.function.Function;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public abstract class GlowAnimalTest<T extends GlowAnimal> extends GlowAgeableTest<T> {
    protected GlowAnimalTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }

    @Test
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.noneOf(Material.class), entity.getBreedingFoods());
    }

    @Test
    public void testFoodSetsLoveMode() {
        InteractEntityMessage interact = new InteractEntityMessage(1,
                InteractEntityMessage.Action.INTERACT.ordinal());
        for (Material foodType : entity.getBreedingFoods()) {
            try {
                entity.setBreed(true);
                ItemStack food = new ItemStack(foodType, 2);
                inventory.setItemInMainHand(food);
                entity.entityInteract(player, interact);

                // Should consume food
                assertEquals(1, inventory.getItemInMainHand().getAmount());

                // Should set love mode
                assertTrue(entity.getInLove() > 0);

                // Using food a 2nd time should not consume it
                entity.entityInteract(player, interact);
                assertEquals(1, inventory.getItemInMainHand().getAmount());
            } finally {
                entity.setInLove(0);
            }
        }
    }

    @Test
    public void testFoodDoesNotSetLoveModeAfterBreeding() {
        InteractEntityMessage interact = new InteractEntityMessage(1,
                InteractEntityMessage.Action.INTERACT.ordinal());
        entity.setAge(1);
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

    @Test(expected = UnsupportedOperationException.class)
    public void testGetBreedingFoodsReturnsImmutableSet() {
        entity.getBreedingFoods().add(Material.SANDSTONE);
    }

    @Test
    @Override
    public void testComputeGrowthAmount() {
        entity.setAge(-21000);
        assertEquals(0, entity.computeGrowthAmount(null));
        assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        for (Material food : entity.getBreedingFoods()) {
            assertEquals(2100, entity.computeGrowthAmount(food), food.name());
        }
    }

    @Test
    public void testComputeGrowthAmountAdult() {
        entity.setAge(0);
        assertEquals(0, entity.computeGrowthAmount(null));
        assertEquals(0, entity.computeGrowthAmount(Material.SAND));

        for (Material food : entity.getBreedingFoods()) {
            assertEquals(0, entity.computeGrowthAmount(food), food.name());
        }
    }
}
