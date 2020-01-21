package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.BrewingStand;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GlowBrewingStand extends AbstractBlockData implements BrewingStand {

    private Map<Integer, Boolean> bottles = new HashMap<>();

    public GlowBrewingStand(){
        this(Material.BREWING_STAND);
    }

    public GlowBrewingStand(Material material){
        this(material, new HashMap.SimpleEntry<>(0, false), new HashMap.SimpleEntry<>(1, false), new HashMap.SimpleEntry<>(2, false));
    }

    public GlowBrewingStand(Material material, Map<Integer, Boolean> map) {
        super(material);
        map.entrySet().forEach(e -> bottles.put(e.getKey(), e.getValue()));
    }

    public GlowBrewingStand(Material material, Map.Entry<Integer, Boolean>... entries) {
        super(material);
        for(int i = 0; i < entries.length; i++){
            this.bottles.put(entries[i].getKey(), entries[i].getValue());
        }
    }

    @Override
    public boolean hasBottle(int i) {
        return this.bottles.get(i);
    }

    @Override
    public void setBottle(int i, boolean b) {
        this.bottles.replace(i, b);
    }

    @Override
    public @NotNull Set<Integer> getBottles() {
        Set<Integer> bottles = new HashSet<>();
        this.bottles.entrySet().stream().filter(e -> e.getValue()).forEach(e -> bottles.add(e.getKey()));
        return Collections.unmodifiableSet(bottles);
    }

    @Override
    public int getMaximumBottles() {
        return this.bottles.size();
    }

    @Override
    public @NotNull String getAsString() {
        String bottles = null;
        for(Map.Entry<Integer, Boolean> entry : this.bottles.entrySet()){
            if(bottles == null){
                bottles = "has_bottle_" + entry.getKey() + ":" + entry.getValue();
            } else {
                bottles = bottles + ", has_bottle_" + entry.getKey() + ":" + entry.getValue();
            }
        }
        if(bottles == null){
            bottles = "";
        }
        return "minecraft:" + this.getMaterial().name().toLowerCase() + " [" + bottles + "]";
    }

    @Override
    public @NotNull BlockData merge(@NotNull BlockData blockData) {
        return null;
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowBrewingStand(this.getMaterial(), this.bottles);
    }
}
