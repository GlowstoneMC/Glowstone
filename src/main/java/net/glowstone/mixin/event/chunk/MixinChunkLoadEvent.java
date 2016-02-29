package net.glowstone.mixin.event.chunk;

import org.bukkit.Chunk;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ChunkLoadEvent.class, remap = false)
public abstract class MixinChunkLoadEvent extends ChunkEvent implements org.spongepowered.api.event.world.chunk.LoadChunkEvent {

    protected MixinChunkLoadEvent(Chunk chunk) {
        super(chunk);
    }

}
