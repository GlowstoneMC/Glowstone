package net.glowstone.net.rcon;

import java.util.Set;
import lombok.Getter;
import net.glowstone.GlowServer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class RconCommandSender implements RemoteConsoleCommandSender {

    @Getter
    private final GlowServer server;
    private final StringBuffer buffer = new StringBuffer();
    private final PermissibleBase perm = new PermissibleBase(this);
    private Spigot spigot = new Spigot() {
        @Override
        public void sendMessage(BaseComponent component) {
            RconCommandSender.this.sendMessage(component);
        }

        @Override
        public void sendMessage(BaseComponent... components) {
            RconCommandSender.this.sendMessage(components);
        }
    };

    public RconCommandSender(GlowServer server) {
        this.server = server;
    }

    /**
     * Empties the buffer and returns its contents.
     *
     * @return the previous contents of the buffer.
     */
    public String flush() {
        String result;
        synchronized (buffer) {
            result = buffer.toString();
            buffer.setLength(0);
        }
        return result;
    }

    @Override
    public String getName() {
        return "Rcon";
    }

    @Override
    public Spigot spigot() {
        return spigot;
    }

    @Override
    public void sendMessage(String message) {
        buffer.append(message).append("\n");
    }

    @Override
    public void sendMessage(String[] strings) {
        for (String line : strings) {
            sendMessage(line);
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // Permissible

    @Override
    public boolean isPermissionSet(String name) {
        return perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return this.perm.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return perm.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return this.perm.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return perm.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return perm.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value,
        int ticks) {
        return perm.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return perm.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        perm.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return perm.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
        throw new UnsupportedOperationException(
            "Cannot change operator status of Rcon command sender");
    }
}
