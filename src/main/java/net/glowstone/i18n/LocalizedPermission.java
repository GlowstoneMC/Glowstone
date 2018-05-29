package net.glowstone.i18n;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;

/**
 * A {@link LocalizedString} that is a permission description, is associated with the permission's
 * config key, and wraps the overloads of {@code registerPermission} in {@link DefaultPermissions}
 * that take key and description parameters and are used by Glowstone.
 */
public interface LocalizedPermission extends LocalizedString {

    /**
     * Registers and returns this permission with its localized description.
     *
     * @see DefaultPermissions#registerPermission(String, String)
     * @return the registered permission
     */
    Permission register();

    /**
     * Registers and returns this permission with its localized description.
     *
     * @see DefaultPermissions#registerPermission(String, String, Permission)
     * @param parent the parent permission
     * @return the registered permission
     */
    Permission register(Permission parent);

    /**
     * Registers and returns this permission with its localized description.
     *
     * @see DefaultPermissions#registerPermission(String, String, PermissionDefault, Permission)
     * @param def the defaulting strategy
     * @param parent the parent permission
     * @return the registered permission
     */
    Permission register(PermissionDefault def, Permission parent);
}
