package net.glowstone.i18n;

public interface LocalizedPermissions {
    LocalizedPermission COMMAND = new LocalizedPermissionImpl("minecraft.command");

    LocalizedPermission ROOT = new LocalizedPermissionImpl("minecraft");

    LocalizedPermission TELL = new LocalizedPermissionImpl("minecraft.command.tell");
}
