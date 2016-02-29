package net.glowstone.mixin.world;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(value = GlowWorld.class, remap = false)
public abstract class MixinGlowWorld implements World {

    @Shadow
    public abstract GlowChunk getChunkAt(int cx, int cz);

    @Override
    public Optional<Chunk> getChunk(int cx, int cy, int cz) {
        return Optional.ofNullable((Chunk) (Object) getChunkAt(cx, cz));
    }
}
