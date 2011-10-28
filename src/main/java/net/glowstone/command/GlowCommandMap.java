package net.glowstone.command;

import net.glowstone.util.StringUtil;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import static org.bukkit.util.Java15Compat.Arrays_copyOfRange;

import java.util.*;

/**
 * SimpleCommandMap that ignores certain "vanilla" commands and supports fuzzy command matching.
 */
public class GlowCommandMap extends SimpleCommandMap {

    private final Server server;
    private static Permission parent;
    private static Permission betterParent;

    public GlowCommandMap(final Server server) {
        super(server);
        this.server = server;
    }

    @Override
    public VanillaCommand getFallback(String name) {
        return null;
    }
    
    public Set<String> getKnownCommandNames() {
        return knownCommands.keySet();
    }

    public Collection<Command> getKnownCommands(boolean includeAliases) {
        if (includeAliases) {
            return knownCommands.values();
        } else {
            return new HashSet<Command>(knownCommands.values());
        }
    }

    /**
     * {@inheritDoc}
     * This is an additional method to allow fuzzy command matching.
     */
    public boolean dispatch(CommandSender sender, String commandLine, boolean fuzzy) throws CommandException {
        String[] args = commandLine.split(" ");

        if (args.length == 0) {
            return false;
        }

        String sentCommandLabel = args[0].toLowerCase();
        Command target = getCommand(sentCommandLabel);
        if (target == null) {
            if (fuzzy) {
                int minDist = -1;

                for (Map.Entry<String, Command> entry : knownCommands.entrySet()) {
                    if (entry.getKey().charAt(0) != sentCommandLabel.charAt(0)) {
                        continue;
                    }

                    int dist = StringUtil.getLevenshteinDistance(entry.getKey().toLowerCase(), sentCommandLabel);

                    if ((dist < minDist || minDist == -1) && dist < 2) {
                        minDist = dist;
                        target = entry.getValue();
                    }
                }
            }
        }
        if (target == null) {
            return false;
        }
        try {
            // Note: we don't return the result of target.execute as that's success / failure, we return handled (true) or not handled (false)
            target.execute(sender, sentCommandLabel, Arrays_copyOfRange(args, 1, args.length));
        } catch (CommandException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new CommandException("Unhandled exception executing '" + commandLine + "' in " + target, ex);
        }

        // return true as command was handled
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean register(String label, String fallbackPrefix, Command command) {
        boolean registeredPassedLabel = register(label, fallbackPrefix, command, false);

        Iterator iterator = command.getAliases().iterator();
        while (iterator.hasNext()) {
            if (!register((String) iterator.next(), fallbackPrefix, command, true)) {
                iterator.remove();
            }
        }

        // Register to us so further updates of the commands label and aliases are postponed until its reregistered
        command.register(this);

        return registeredPassedLabel;
    }

    /**
     * {@inheritDoc}
     * This is overridden so that command aliases override Glowstone commands
     */
    private synchronized boolean register(String label, String fallbackPrefix, Command command, boolean isAlias) {
        String lowerLabel = label.trim().toLowerCase();
        if (isAlias && getIgnoringVanilla(lowerLabel) != null) {
            // Request is for an alias and it conflicts with a existing command or previous alias ignore it
            // Note: This will mean it gets removed from the commands list of active aliases
            return false;
        }

        String lowerPrefix = fallbackPrefix.trim().toLowerCase();
        boolean registerdPassedLabel = true;

        // If the command exists but is an alias we overwrite it, otherwise we rename it based on the fallbackPrefix
        while (getIgnoringVanilla(lowerLabel) != null && !aliases.contains(lowerLabel)) {
            lowerLabel = lowerPrefix + ":" + lowerLabel;
            registerdPassedLabel = false;
        }

        if (isAlias) {
            aliases.add(lowerLabel);
        } else {
            // Ensure lowerLabel isn't listed as a alias anymore and update the commands registered name
            aliases.remove(lowerLabel);
            command.setLabel(lowerLabel);
        }
        knownCommands.put(lowerLabel, command);

        return registerdPassedLabel;
    }

    public Command getIgnoringVanilla(String label) {
        Command cmd = knownCommands.get(label);
        if (cmd instanceof GlowCommand) return null;
        return cmd;
    }

    /**
     * Perpares the glowstone command permissions. Should be only run once per plugin manager
     */
    public static void initGlowPermissions(Server server) {
        PluginManager pm = server.getPluginManager();
        parent = new Permission(GlowCommand.PERM_PREFIX, "Gives access to all Glowstone commads");
        betterParent = new Permission("glowstone", "Gives access to all Glowstone commands and functions");
        betterParent.getChildren().put(parent.getName(), true);
        pm.addPermission(betterParent);
        pm.addPermission(parent);
        parent.recalculatePermissibles();
        betterParent.recalculatePermissibles();
    }

    public void registerAll(Collection<GlowCommand> commands) {
        for (GlowCommand command : commands) {
            register(command);
        }
    }

    /**
     * Registers GlowCommands using the extra information available from them
     * @param command
     */
    public void register(GlowCommand command) {
        registerPermissions(command);
        register("#", command);
    }

    public void registerPermissions(GlowCommand command) {
        PluginManager pm = server.getPluginManager();
        Permission child = new Permission(GlowCommand.PERM_PREFIX + "." + command.getName(), command.getPermissionDefault());
        for (Permission permission : command.registerPermissions(child.getName())) {
            child.getChildren().put(permission.getName(), true);
            if (pm.getPermission(permission.getName()) == null) {
                pm.addPermission(permission);
            }
            permission.recalculatePermissibles();
        }
        parent.getChildren().put(child.getName(), true);
        if (pm.getPermission(child.getName()) == null) {
            pm.addPermission(child);
        }
        command.setPermission(child.getName());
        child.recalculatePermissibles();
        parent.recalculatePermissibles();
    }

    public void registerAllPermissions() {
        for (Command command : knownCommands.values()) {
            if (command instanceof GlowCommand) {
                registerPermissions((GlowCommand) command);
            }
        }
    }

    /**
     * Returns a list of command permissions registered through this command map.
     */
    public Set<String> getCommandPermissions() {
        HashSet<String> perms = new HashSet<String>();
        perms.add(parent.getName());
        perms.add(betterParent.getName());
        for (Command command : knownCommands.values()) {
            perms.add(command.getPermission());
            if (command instanceof GlowCommand) {
                for (Permission perm : ((GlowCommand) command).registerPermissions(parent.getName())) {
                    perms.add(perm.getName());
                }
            }
        }
        return perms;
    }

    public void removeAllOfType(Class<? extends Command> clazz) {
        List<String> toRemove = new ArrayList<String>();
        for (Iterator<Command> i = knownCommands.values().iterator(); i.hasNext();) {
            Command cmd = i.next();
            if (clazz.isAssignableFrom(cmd.getClass())) {
                i.remove();
                for (String alias : cmd.getAliases()) {
                    Command aliasCmd = knownCommands.get(alias);
                    if (cmd.equals(aliasCmd)) {
                        aliases.remove(alias);
                        toRemove.add(alias);
                    }
                }
            }
        }
        for (String string : toRemove) {
            knownCommands.remove(string);
        }
    }

}
