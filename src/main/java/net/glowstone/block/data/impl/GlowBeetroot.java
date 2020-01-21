package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowBeetroot extends AbstractBlockData implements Ageable {

    private int age;

    public GlowBeetroot(Material material){
        this(material, 0);
    }

    public GlowBeetroot(Material material, int age) {
        super(material);
        this.setAge(age);
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public void setAge(int i) {
        if(i > this.getMaximumAge()){
            throw new UnsupportedOperationException("Age of " + i + " is greater then max age of " + this.getMaximumAge());
        }
        this.age = i;
    }

    @Override
    public int getMaximumAge() {
        return 3;
    }

    @Override
    public @NotNull String getAsString() {
        return "minecraft:" + this.getMaterial().name().toLowerCase() + "[age:" + this.age + "]";
    }

    @Override
    public @NotNull BlockData merge(@NotNull BlockData blockData) {
        return null;
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowBeetroot(this.getMaterial(), this.age);
    }
}
