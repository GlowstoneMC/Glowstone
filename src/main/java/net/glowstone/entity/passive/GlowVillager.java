package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAgeable;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;
import java.util.Random;

public class GlowVillager extends GlowAgeable implements Villager {

    private Profession profession;

    public GlowVillager(Location location) {
        super(location, EntityType.VILLAGER, 20);
        Random r = new Random();
        setProfession(Profession.values()[r.nextInt(Profession.values().length - 2) + 1]);
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
    }

    @Override
    public List<MerchantRecipe> getRecipes() {
        return null;
    }

    @Override
    public void setRecipes(List<MerchantRecipe> list) {

    }

    @Override
    public MerchantRecipe getRecipe(int i) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public void setRecipe(int i, MerchantRecipe merchantRecipe) throws IndexOutOfBoundsException {

    }

    @Override
    public int getRecipeCount() {
        return 0;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @Override
    public boolean isTrading() {
        return false;
    }

    @Override
    public HumanEntity getTrader() {
        return null;
    }

    @Override
    public int getRiches() {
        return 0;
    }

    @Override
    public void setRiches(int i) {

    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_VILLAGER_DEATH;
    }
}
