package net.glowstone.entity.passive;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import lombok.Getter;
import lombok.Setter;
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

public class GlowVillager extends GlowAgeable implements Villager {

    private static final Profession[] PROFESSIONS = Profession.values();

    @Getter
    private Career career;
    @Getter
    @Setter
    private int riches;
    /**
     * The trader this villager is currently trading with.
     *
     * @param trader the trader
     */
    @Getter
    @Setter
    private GlowHumanEntity trader;
    private List<MerchantRecipe> recipes = new ArrayList<>();
    /**
     * Whether or not this villager is willing to mate.
     *
     * @param willing true if this villager is willing to mate, false otherwise
     * @return true if this villager is willing to mate, false otherwise
     */
    @Getter
    @Setter
    private boolean willing;
    /**
     * Get the current level of this villager's trading options.
     *
     * @return the current level of this villager's trading options
     */
    @Getter
    @Setter
    private int careerLevel;

    /**
     * Creates a villager with a random profession.
     *
     * @param location the location
     */
    public GlowVillager(Location location) {
        super(location, EntityType.VILLAGER, 20);
        setProfession(getRandomProfession(ThreadLocalRandom.current()));
        setBoundingBox(0.6, 1.95);
    }

    @Override
    public Profession getProfession() {
        return PROFESSIONS[metadata.getInt(MetadataIndex.VILLAGER_PROFESSION)];
    }

    @Override
    public void setProfession(Profession profession) {
        checkArgument(profession != Profession.HUSK);

        metadata.set(MetadataIndex.VILLAGER_PROFESSION, profession.ordinal());
        assignCareer();
    }

    @Override
    public void setCareer(Career career) {
        Profession profession = getProfession();
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
        Profession profession = getProfession();
        if (profession == null || profession.isZombie()) {
            this.career = null;
        } else {
            Career[] careers = getCareersByProfession(profession);
            this.career = careers[ThreadLocalRandom.current().nextInt(careers.length)];
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
     * @param id the id of the career
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

    /**
     * Gets a random {@link Villager.Profession}.
     *
     * @param random the random instance
     * @return a random {@link Villager.Profession}
     */
    public static Profession getRandomProfession(Random random) {
        checkNotNull(random);
        // Ignore HUSK profession (deprecated)
        return PROFESSIONS[random.nextInt(PROFESSIONS.length - 2)];
    }

    /**
     * Checks whether or not the given {@link Villager.Profession} ID is valid.
     *
     * @param professionId the ID of the {@link Villager.Profession}
     * @return true if the ID is valid, false otherwise
     */
    public static boolean isValidProfession(int professionId) {
        return professionId >= 0 && professionId < PROFESSIONS.length - 1;
    }

    /**
     * Gets the {@link Villager.Profession} corresponding to the given ID.
     *
     * @param professionId the ID of the {@link Villager.Profession}
     * @return the corresponding {@link Villager.Profession}, or null if none exists
     */
    public static Profession getProfessionById(int professionId) {
        if (!isValidProfession(professionId)) {
            return null;
        }
        return PROFESSIONS[professionId];
    }
}
