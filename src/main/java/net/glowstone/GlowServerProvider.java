package net.glowstone;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Server;

/**
 * Used to replace the {@link GlowServer} instance for testing purposes, when {@link
 * org.bukkit.Bukkit#setServer(Server)} has already been called.
 */
public final class GlowServerProvider {
    /**
     * The current mock GlowServer instance, or null if not using a mock server.
     */
    @Getter
    @Setter
    private static volatile Server mockServer;

    /**
     * Returns the current GlowServer instance.
     * @return the GlowServer instance
     */
    public static Server getServer() {
        return mockServer == null ? Bukkit.getServer() : mockServer;
    }
}
