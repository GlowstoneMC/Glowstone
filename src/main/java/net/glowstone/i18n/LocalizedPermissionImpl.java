package net.glowstone.i18n;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;
import org.jetbrains.annotations.NonNls;

public class LocalizedPermissionImpl extends LocalizedStringImpl implements LocalizedPermission {
    @NonNls private final String key;

    public LocalizedPermissionImpl(@NonNls String key) {
        super("glowstone.permission.desc." + key);
        this.key = key;
    }

    @Override
    public Permission register() {
        return DefaultPermissions.registerPermission(key, get());
    }

    @Override
    public Permission register(Permission parent) {
        return DefaultPermissions.registerPermission(key, get(), parent);
    }

    @Override
    public Permission register(PermissionDefault def, Permission parent) {
        return DefaultPermissions.registerPermission(key, get(), def, parent);
    }
}
