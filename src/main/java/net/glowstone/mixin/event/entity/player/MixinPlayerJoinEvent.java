package net.glowstone.mixin.event.entity.player;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(value = PlayerJoinEvent.class, remap = false)
public abstract class MixinPlayerJoinEvent extends PlayerEvent implements ClientConnectionEvent.Join {

    private Optional<Text> originalMessage;

    @Shadow
    private String joinMessage;

    public MixinPlayerJoinEvent(Player who) {
        super(who);
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    public void onConstruct(CallbackInfo callback) {
        originalMessage = Optional.of(Text.of(joinMessage));
    }

    @Override
    public MessageChannel getOriginalChannel() {
        return null;
    }

    @Override
    public Optional<MessageChannel> getChannel() {
        return null;
    }

    @Override
    public void setChannel(@Nullable MessageChannel messageChannel) {

    }

    @Override
    public Optional<Text> getOriginalMessage() {
        return originalMessage;
    }

    @Override
    public Optional<Text> getMessage() {
        if (joinMessage != null) {
            return Optional.of(Text.of(joinMessage));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void setMessage(@Nullable Text text) {
        if (text == null) {
            joinMessage = null;
        } else {
            joinMessage = text.toPlain();
        }
    }

    @Override
    public org.spongepowered.api.entity.living.player.Player getTargetEntity() {
        return (org.spongepowered.api.entity.living.player.Player) getPlayer();
    }
}
