package net.glowstone.mixin.world;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GlowChunk.class, remap = false)
public abstract class MixinGlowChunk implements Chunk {

    @Shadow
    public abstract GlowWorld shadow$getWorld();

    @Shadow
    public abstract GlowBlock shadow$getBlock(int x, int y, int z);

    @Override
    public World getWorld() {
        return (World) (Object) shadow$getWorld();
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        return (BlockState) (Object) shadow$getBlock(x, y, z);
    }
}
