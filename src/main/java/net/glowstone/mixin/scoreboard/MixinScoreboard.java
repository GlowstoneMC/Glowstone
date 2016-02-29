package net.glowstone.mixin.scoreboard;

import net.glowstone.scoreboard.GlowScoreboard;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GlowScoreboard.class)
public abstract class MixinScoreboard implements Scoreboard {

}
