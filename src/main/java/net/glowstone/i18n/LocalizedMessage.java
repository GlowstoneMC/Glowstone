package net.glowstone.i18n;

public enum LocalizedMessage {
    
    // Command errors
    COMMAND_NOT_FOUND("commands.notFound", "Unknown command: %s"),
    COMMAND_PERMISSION_DENIED("commands.noPermission", "You don't have permission to use this command"),
    COMMAND_PLAYER_ONLY("commands.playerOnly", "This command can only be used by players"),
    
    // Version mismatch
    VERSION_MISMATCH("version.mismatch", "Your version (%s) doesn't match the server version (%s)"),
    VERSION_OUTDATED("version.outdated", "Your client is outdated. Please update to version %s"),
    VERSION_NEWER("version.newer", "Your client is too new. Please update to version %s"),
    
    // Connection errors
    CONNECTION_CLOSED("connection.closed", "Disconnected: %s"),
    CONNECTION_TIMEOUT("connection.timeout", "Timed out"),
    CONNECTION_KICKED("connection.kicked", "Kicked from server: %s"),
    
    // Chat messages
    CHAT_FORMAT("chat.format", "<%s> %s"),
    CHAT_SAY("chat.say", "%s says: %s"),
    
    // Server messages
    SERVER_STARTING("server.starting", "Starting Glowstone server..."),
    SERVER_READY("server.ready", "Server is ready!"),
    SERVER_STOPPING("server.stopping", "Stopping server..."),
    SERVER_ERROR("server.error", "An error occurred: %s"),
    
    // Player messages
    PLAYER_JOIN("player.join", "%s joined the game"),
    PLAYER_LEAVE("player.leave", "%s left the game"),
    PLAYER_GAME_MODE_CHANGED("player.gameModeChanged", "%s is now in %s mode"),
    
    // World messages
    WORLD_LOAD_ERROR("world.loadError", "Failed to load world: %s"),
    WORLD_UNLOAD("world.unload", "Unloading world: %s"),
    
    // Item messages
    ITEM_NOT_FOUND("item.notFound", "Unknown item: %s"),
    
    // General
    ERROR_GENERAL("error.general", "Error: %s"),
    WARN_GENERAL("warn.general", "Warning: %s"),
    INFO_GENERAL("info.general", "Info: %s"),
    
    private final String key;
    private final String defaultValue;
    
    LocalizedMessage(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }
    
    /**
     * Get translated message with no arguments
     */
    public String getMessage() {
        return I18nManager.translate(key);
    }
    
    /**
     * Get translated message with arguments
     */
    public String getMessage(Object... args) {
        return I18nManager.translate(key, args);
    }
    
    /**
     * Get the translation key
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Get the default value (fallback)
     */
    public String getDefaultValue() {
        return defaultValue;
    }
}
