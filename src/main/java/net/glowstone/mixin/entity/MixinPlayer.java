package net.glowstone.mixin.entity;

import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Implements(value = @Interface(iface = org.spongepowered.api.entity.living.player.Player.class, prefix = "sponge$"))
@Mixin(GlowPlayer.class)
public abstract class MixinPlayer extends GlowLivingEntity {

    protected MixinPlayer(Location location, double maxHealth) {
        super(location, maxHealth);
    }

    @Shadow(remap = false)
    public abstract org.bukkit.scoreboard.Scoreboard getScoreboard();

    // CHECKSTYLE:OFF:
    public Scoreboard sponge$getScoreboard() {
        return (Scoreboard) getScoreboard();
    }
    // CHECKSTYLE:ON:

}
