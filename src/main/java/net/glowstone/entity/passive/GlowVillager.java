package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAgeable;
import net.glowstone.entity.GlowHumanEntity;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GlowVillager extends GlowAgeable implements Villager {

    private Profession profession;
    private Career career;
    private int riches;
    private GlowHumanEntity trader;
    private List<MerchantRecipe> recipes = new ArrayList<>();
    private boolean willing;
    private int careerLevel;

    public GlowVillager(Location location) {
        super(location, EntityType.VILLAGER, 20);
        Random random = ThreadLocalRandom.current();
        setProfession(Profession.values()[random.nextInt(Profession.values().length - 2) + 1]);
        setBoundingBox(0.6, 1.95);
    }

    @Override
    public Profession getProfession() {
        return profession;
    }

    @Override
    public void setProfession(Profession profession) {
        this.profession = profession;
        metadata.set(MetadataIndex.VILLAGER_PROFESSION, profession.ordinal() - 1);
        assignCareer();
    }

    @Override
    public Career getCareer() {
        return career;
    }

    @Override
    public void setCareer(Career career) {
        if (profession == null || profession.isZombie()) {
            return;
        }
        if (career == null) {
            assignCareer();
            return;
        }
        if (career.getProfession() != profession) {
            setProfession(profession);
        }
        this.career = career;
        this.careerLevel = 1;
    }

    @Override
    public List<MerchantRecipe> getRecipes() {
        return new ArrayList<>(recipes);
    }

    @Override
    public void setRecipes(List<MerchantRecipe> recipes) {
        this.recipes = new ArrayList<>(recipes);
    }

    @Override
    public MerchantRecipe getRecipe(int index) throws IndexOutOfBoundsException {
        return recipes.get(index);
    }

    @Override
    public void setRecipe(int index, MerchantRecipe recipe) throws IndexOutOfBoundsException {
        recipes.set(index, recipe);
    }

    @Override
    public int getRecipeCount() {
        return recipes.size();
    }

    /**
     * Clears the recipes of this villager.
     */
    public void clearRecipes() {
        recipes.clear();
    }

    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isTrading() {
        return getTrader() != null;
    }

    @Override
    public GlowHumanEntity getTrader() {
        return trader;
    }

    /**
     * Sets the trader this villager is currently trading with.
     *
     * @param trader the trader
     */
    public void setTrader(GlowHumanEntity trader) {
        this.trader = trader;
    }

    @Override
    public int getRiches() {
        return riches;
    }

    @Override
    public void setRiches(int riches) {
        this.riches = riches;
    }

    /**
     * Get whether or not this villager is willing to mate.
     *
     * @return true if this villager is willing to mate, false otherwise
     */
    public boolean isWilling() {
        return willing;
    }

    /**
     * Sets whether or not this villager is willing to mate.
     *
     * @param willing true if this villager is willing to mate, false otherwise
     */
    public void setWilling(boolean willing) {
        this.willing = willing;
    }

    /**
     * Get the current level of this villager's trading options.
     *
     * @return the current level of this villager's trading options
     */
    public int getCareerLevel() {
        return careerLevel;
    }

    /**
     * Set the current level of this villager's trading options.
     * <br>
     * If 0, the next trade will assign a new career and set the career level to 1.
     *
     * @param careerLevel the level of this villager's trading options
     */
    public void setCareerLevel(int careerLevel) {
        this.careerLevel = careerLevel;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_VILLAGER_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_VILLAGER_AMBIENT;
    }

    @Override
    public void damage(double amount, Entity source, DamageCause cause) {
        if (!DamageCause.LIGHTNING.equals(cause)) {
            super.damage(amount, source, cause);
            return;
        }

        Witch witch = world.spawn(this.location, Witch.class);
        witch.damage(amount, source, cause);
        witch.setFireTicks(this.getFireTicks());
        remove();
    }

    /**
     * Assigns a random career to the villager.
     */
    private void assignCareer() {
        if (profession == null || profession.isZombie()) {
            this.career = null;
        } else {
            Random random = ThreadLocalRandom.current();
            Career[] careers = getCareersByProfession(profession);
            this.career = careers[random.nextInt(careers.length)];
            this.careerLevel = 1;
        }
    }

    /**
     * Gets all assignable careers for a given profession.
     *
     * @param profession the profession
     * @return the assignable careers for the given profession
     */
    public static Career[] getCareersByProfession(Profession profession) {
        return Arrays.stream(Career.values())
                .filter(c -> c.getProfession() == profession)
                .toArray(Career[]::new);
    }

    /**
     * Gets the career associated with a given ID and profession.
     *
     * @param id         the id of the career
     * @param profession the profession
     * @return the career associated with the given ID and profession
     */
    public static Career getCareerById(int id, Profession profession) {
        if (profession == null || profession.isZombie()) {
            return null;
        }
        return Arrays.stream(Career.values())
                .filter(career -> career.getProfession() == profession)
                .filter(career -> career.getId() == id)
                .findFirst().orElse(null);
    }
}
