package net.glowstone.mixin.event.entity.player;

import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.interfaces.event.player.IAsyncPlayerPreLoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

@Mixin(value = AsyncPlayerPreLoginEvent.class, remap = false)
public abstract class MixinAsyncPlayerPreLoginEvent implements ClientConnectionEvent.Auth, IAsyncPlayerPreLoginEvent {

    private RemoteConnection connection;
    private Optional<Text> originalMessage;

    @Shadow
    private AsyncPlayerPreLoginEvent.Result result;
    @Shadow
    private String message;
    @Shadow
    private String name;
    @Shadow
    private UUID uniqueId;

    public void init(RemoteConnection connection) {
        this.connection = connection;
        this.originalMessage = Optional.of(Text.of(message));
    }

    @Override
    public RemoteConnection getConnection() {
        return connection;
    }

    @Override
    public GameProfile getProfile() {
        return (GameProfile) (Object) new PlayerProfile(name, uniqueId);
    }

    @Override
    public boolean isCancelled() {
        return result != AsyncPlayerPreLoginEvent.Result.ALLOWED;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        result = cancelled ? AsyncPlayerPreLoginEvent.Result.KICK_OTHER : AsyncPlayerPreLoginEvent.Result.ALLOWED;
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
}
