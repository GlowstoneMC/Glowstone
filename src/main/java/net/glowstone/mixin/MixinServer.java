package net.glowstone.mixin;

import net.glowstone.GlowServer;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

@Mixin(GlowServer.class)
public abstract class MixinServer implements Game, Server {

    @Shadow(remap = false)
    public abstract String shadow$getMotd();

    @Shadow(remap = false)
    public abstract org.bukkit.entity.Player shadow$getPlayer(UUID uuid);

    @Shadow(remap = false)
    public abstract org.bukkit.entity.Player shadow$getPlayer(String name);

    @Shadow(remap = false)
    public abstract org.bukkit.plugin.PluginManager shadow$getPluginManager();

    @Shadow(remap = false)
    public abstract InetSocketAddress shadow$getBoundAddress();

    @Override
    public PluginManager getPluginManager() {
        return (PluginManager) shadow$getPluginManager();
    }

    @Override
    public Text getMotd() {
        return Text.of(shadow$getMotd());
    }

    @Override
    public Optional<InetSocketAddress> getBoundAddress() {
        return Optional.ofNullable(shadow$getBoundAddress());
    }

    @Override
    public Server getServer() {
        return this;
    }

    @Override
    public Optional<Player> getPlayer(String name) {
        return Optional.ofNullable((Player) shadow$getPlayer(name));
    }

    @Override
    public Optional<Player> getPlayer(UUID uid) {
        return Optional.ofNullable((Player) shadow$getPlayer(uid));
    }
}
