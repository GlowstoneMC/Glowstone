package net.glowstone.entity.passive;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowAgeable;
import net.glowstone.entity.GlowHumanEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.inventory.GlowMerchantInventory;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.PluginMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class GlowVillager extends GlowAgeable implements Villager {

    private static final Profession[] PROFESSIONS = Profession.values();
    private static final MerchantRecipe DEFAULT_RECIPE
            = new MerchantRecipe(new ItemStack(Material.DIRT), 10);
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

        // add dummy recipe
        // todo: recipe loading and randomization
        this.recipes.add(DEFAULT_RECIPE);
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
        setCareer(career, true);
    }

    @Override
    public void setCareer(Career career, boolean resetTrades) {
        // todo: implement resetTrades
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

    /**
     * Set the current level of this villager's trading options.
     *
     * <p>If 0, the next trade will assign a new career and set the career level to 1.
     *
     * @param careerLevel the level of this villager's trading options
     */
    public void setCareerLevel(int careerLevel) {
        this.careerLevel = careerLevel;
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        super.entityInteract(player, message);
        if (message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            if (this.recipes.isEmpty()) {
                GlowServer.logger.info(
                        player.getName() + " tried trading with a villager with no recipes.");
                return false;
            }
            // open merchant view
            GlowMerchantInventory merchantInventory = new GlowMerchantInventory(player, this);
            InventoryView view = player.openInventory(merchantInventory);
            if (view != null) {
                // send recipes (plugin channel)
                sendRecipes(merchantInventory, player);
                return true;
            }
        }
        return false;
    }

    private void sendRecipes(GlowMerchantInventory inventory, GlowPlayer player) {
        // TODO: Move this to a new 'GlowMerchant' class, to allow custom Merchant windows
        checkNotNull(inventory);
        checkNotNull(player);

        int windowId = player.getOpenWindowId();
        if (windowId == -1) {
            return;
        }

        ByteBuf payload = Unpooled.buffer();
        payload.writeInt(windowId);
        payload.writeByte(this.recipes.size());
        for (MerchantRecipe recipe : this.recipes) {
            if (recipe.getIngredients().isEmpty()) {
                GlowBufUtils.writeSlot(payload, InventoryUtil.createEmptyStack());
            } else {
                GlowBufUtils.writeSlot(payload, recipe.getIngredients().get(0));
            }
            GlowBufUtils.writeSlot(payload, recipe.getResult());
            boolean secondIngredient = recipe.getIngredients().size() > 1;
            payload.writeBoolean(secondIngredient);
            if (secondIngredient) {
                GlowBufUtils.writeSlot(payload, recipe.getIngredients().get(1));
            }
            payload.writeBoolean(false); // todo: no isDisabled() in MerchantRecipe?
            payload.writeInt(recipe.getUses());
            payload.writeInt(recipe.getMaxUses());
        }
        player.getSession().send(new PluginMessage("MC|TrList", payload.array()));
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
            List<Career> careers = profession.getCareers();
            this.career = careers.get(ThreadLocalRandom.current().nextInt(careers.size()));
            this.careerLevel = 1;
        }
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
        if (id >= profession.getCareers().size()) {
            return null;
        }
        return profession.getCareers().get(id);
    }

    /**
     * Gets the numerical ID of the given career.
     *
     * @param career the career
     * @return the id of the career
     */
    public static int getCareerId(Career career) {
        checkNotNull(career);

        Profession profession = career.getProfession();
        return profession.getCareers().indexOf(career);
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

    static {
        DEFAULT_RECIPE.addIngredient(new ItemStack(Material.COBBLESTONE));
    }
}
