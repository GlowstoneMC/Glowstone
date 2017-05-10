package net.glowstone;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Handles all logging and input-related console improvements.
 */
public final class ConsoleManager {

    private GlowServer server;
    LineReader reader;

    private ConsoleCommandSender sender;

    boolean running;

    private static final Map<ChatColor, String> replacements = new EnumMap<>(ChatColor.class);

    static {
        addReplacement(ChatColor.BLACK, "\u001B[0;30;22m");
        addReplacement(ChatColor.DARK_BLUE, "\u001B[0;34;22m");
        addReplacement(ChatColor.DARK_GREEN, "\u001B[0;32;22m");
        addReplacement(ChatColor.DARK_AQUA, "\u001B[0;36;22m");
        addReplacement(ChatColor.DARK_RED, "\u001B[0;31;22m");
        addReplacement(ChatColor.DARK_PURPLE, "\u001B[0;35;22m");
        addReplacement(ChatColor.GOLD, "\u001B[0;33;22m");
        addReplacement(ChatColor.GRAY, "\u001B[0;37;22m");
        addReplacement(ChatColor.DARK_GRAY, "\u001B[0;30;1m");
        addReplacement(ChatColor.BLUE, "\u001B[0;34;1m");
        addReplacement(ChatColor.GREEN, "\u001B[0;32;1m");
        addReplacement(ChatColor.AQUA, "\u001B[0;36;1m");
        addReplacement(ChatColor.RED, "\u001B[0;31;1m");
        addReplacement(ChatColor.LIGHT_PURPLE, "\u001B[0;35;1m");
        addReplacement(ChatColor.YELLOW, "\u001B[0;33;1m");
        addReplacement(ChatColor.WHITE, "\u001B[0;37;1m");
        addReplacement(ChatColor.MAGIC, "\u001B[5m");
        addReplacement(ChatColor.BOLD, "\u001B[21m");
        addReplacement(ChatColor.STRIKETHROUGH, "\u001B[9m");
        addReplacement(ChatColor.UNDERLINE, "\u001B[4m");
        addReplacement(ChatColor.ITALIC, "\u001B[3m");
        addReplacement(ChatColor.RESET, "\u001B[39;0m");
    }

    private static void addReplacement(ChatColor formatting, String ansi) {
        replacements.put(formatting, ansi);
    }

    public ConsoleManager(GlowServer server) {
        this.server = server;

        try (Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .name("Glowstone")
                    .build()) {
            reader = LineReaderBuilder.builder()
                    .appName("Glowstone")
                    .terminal(terminal)
                    .completer(new CommandCompleter())
                    .build();
            reader.unsetOpt(LineReader.Option.INSERT_TAB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        sender = new ColoredCommandSender();
        if (!running) {
            running = true;
            new ConsoleCommandThread().start();
        }
    }

    public void stop() {
        running = false;
    }

    public ConsoleCommandSender getSender() {
        return sender;
    }

    private class CommandCompleter implements Completer {
        @Override
        public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            List<String> completions = null;
            try {
                completions = server.getScheduler().syncIfNeeded(() -> server.getCommandMap().tabComplete(sender, line.line()));
            } catch (Exception e) {
                GlowServer.logger.log(Level.WARNING, "Error while tab completing", e);
            }

            if (completions != null) {
                completions.forEach(completion -> candidates.add(new Candidate(completion)));
            }
        }
    }

    private class ConsoleCommandThread extends Thread {
        ConsoleCommandThread() {
            setName("ConsoleCommandThread");
            setDaemon(true);
        }

        @Override
        public void run() {
            String command = null;
            while (running && !Thread.interrupted()) {
                try {
                    command = reader.readLine();
                } catch (CommandException e) {
                    GlowServer.logger.log(Level.WARNING, "Exception while executing command: " + command, e);
                } catch (Exception e) {
                    GlowServer.logger.log(Level.SEVERE, "Error while reading commands", e);
                }

                if (command != null && !(command = command.trim()).isEmpty()) {
                    server.getScheduler().runTaskAsynchronously(null, new CommandTask(command));
                }
            }
        }
    }

    private class CommandTask implements Runnable {
        private final String command;

        CommandTask(String command) {
            this.command = command;
        }

        @Override
        public void run() {
            ServerCommandEvent event = EventFactory.callEvent(new ServerCommandEvent(sender, command));
            server.dispatchCommand(sender, event.getCommand());
        }
    }

    private class ColoredCommandSender implements ConsoleCommandSender {
        private final PermissibleBase perm = new PermissibleBase(this);

        ////////////////////////////////////////////////////////////////////////
        // CommandSender

        @Override
        public String getName() {
            return "CONSOLE";
        }

        @Override
        public void sendMessage(String text) {
            server.getLogger().info(text);
        }

        @Override
        public void sendMessage(String[] strings) {
            for (String line : strings) {
                sendMessage(line);
            }
        }

        @Override
        public GlowServer getServer() {
            return server;
        }

        @Override
        public boolean isOp() {
            return true;
        }

        @Override
        public void setOp(boolean value) {
            throw new UnsupportedOperationException("Cannot change operator status of server console");
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
        public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
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

        ////////////////////////////////////////////////////////////////////////
        // Conversable

        @Override
        public boolean isConversing() {
            return false;
        }

        @Override
        public void acceptConversationInput(String input) {

        }

        @Override
        public boolean beginConversation(Conversation conversation) {
            return false;
        }

        @Override
        public void abandonConversation(Conversation conversation) {

        }

        @Override
        public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {

        }

        @Override
        public void sendRawMessage(String message) {

        }
    }
}
