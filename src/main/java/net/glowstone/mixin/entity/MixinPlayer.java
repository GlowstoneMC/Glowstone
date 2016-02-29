package net.glowstone.mixin.entity;

import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.text.Text;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Implements(value = @Interface(iface = org.spongepowered.api.entity.living.player.Player.class, prefix = "sponge$"))
@Mixin(value = GlowPlayer.class, remap = false)
public abstract class MixinPlayer extends GlowLivingEntity {

    protected MixinPlayer(Location location, double maxHealth) {
        super(location, maxHealth);
    }

    @Shadow
    public abstract org.bukkit.scoreboard.Scoreboard getScoreboard();

    @Shadow
    public abstract void kickPlayer(String message);

    public Scoreboard sponge$getScoreboard() {
        return (Scoreboard) getScoreboard();
    }

    public void kick() {
        kickPlayer("");
    }

    public void kick(Text message) {
        kickPlayer(message.toPlain());
    }

}
