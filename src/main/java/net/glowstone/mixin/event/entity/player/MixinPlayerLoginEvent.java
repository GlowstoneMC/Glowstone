package net.glowstone.mixin.event.entity.player;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(value = PlayerLoginEvent.class, remap = false)
public abstract class MixinPlayerLoginEvent extends PlayerEvent implements ClientConnectionEvent.Login {

    private Optional<Text> originalMessage;

    @Shadow private PlayerLoginEvent.Result result;
    @Shadow private String message;

    public MixinPlayerLoginEvent(Player who) {
        super(who);
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    public void onConstruct(CallbackInfo callback) {
        originalMessage = Optional.of(Text.of(message));
    }

    @Override
    public RemoteConnection getConnection() {
        return ((GlowPlayer) getPlayer()).getSession();
    }

    @Override
    public GameProfile getProfile() {
        return (GameProfile) (Object) ((GlowPlayer) getPlayer()).getProfile();
    }

    @Override
    public Transform<World> getFromTransform() {
        return null;
    }

    @Override
    public Transform<World> getToTransform() {
        return null;
    }

    @Override
    public void setToTransform(Transform<World> transform) {

    }

    @Override
    public boolean isCancelled() {
        return result != PlayerLoginEvent.Result.ALLOWED;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        result = cancelled ? PlayerLoginEvent.Result.KICK_OTHER : PlayerLoginEvent.Result.ALLOWED;
    }

    @Override
    public Optional<Text> getOriginalMessage() {
        return originalMessage;
    }

    @Override
    public Optional<Text> getMessage() {
        if (message != null) {
            return Optional.of(Text.of(message));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void setMessage(@Nullable Text text) {
        if (text == null) {
            message = null;
        } else {
            message = text.toPlain();
        }
    }

    @Override
    public User getTargetUser() {
        return (User) getPlayer();
    }
}
