package net.glowstone.generator.decorators;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public abstract class BlockDecorator extends BlockPopulator {

    protected int amount;

    public final void setAmount(int amount) {
        this.amount = amount;
    }

    public abstract void decorate(World world, Random random, Chunk chunk);

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for (int i = 0; i < amount; i++) {
            decorate(world, random, chunk);
        }
    }
}
