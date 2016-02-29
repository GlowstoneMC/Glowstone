package net.glowstone.mixin.block;

import net.glowstone.block.GlowBlock;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GlowBlock.class, remap = false)
public abstract class MixinGlowBlock implements BlockState {

}
