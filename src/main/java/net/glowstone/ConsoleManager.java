package net.glowstone;

import net.glowstone.util.compiler.EvalTask;
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
import org.fusesource.jansi.AnsiConsole;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handles all logging and input-related console improvements.
 */
public final class ConsoleManager {

    private GlowServer server;
    protected LineReader reader;
    private boolean color;

    private ConsoleCommandSender sender;

    protected boolean running;

    private ConsoleHandler handler;
    private static String CONSOLE_DATE = "HH:mm:ss";

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

    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");

    public ConsoleManager(GlowServer server) {
        if (IS_WINDOWS) {
            AnsiConsole.systemInstall();
        }
        this.server = server;
        GlowServer.logger.setUseParentHandlers(false);
        handler = new ConsoleHandler();
        handler.setFormatter(new DateOutputFormatter(CONSOLE_DATE, false));

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
            color = IS_WINDOWS || !Objects.equals(terminal.getType(), Terminal.TYPE_DUMB);
        } catch (IOException e) {
            e.printStackTrace();
        }

        handler.setFormatter(new DateOutputFormatter(CONSOLE_DATE, color));
    }

    public void start() {
        sender = new ColoredCommandSender();
        CONSOLE_DATE = server.getConsoleDateFormat();
        handler.setFormatter(new DateOutputFormatter(CONSOLE_DATE, color));
        GlowServer.logger.addHandler(handler);
        if (!running) {
            running = true;
            new ConsoleCommandThread().start();
        }
    }

    public void stop() {
        running = false;
        if (IS_WINDOWS) {
            AnsiConsole.systemUninstall();
        }
    }

    public ConsoleCommandSender getSender() {
        return sender;
    }

    private class DateOutputFormatter extends Formatter {
        private final SimpleDateFormat date;
        private final boolean color;

        public DateOutputFormatter(String pattern, boolean color) {
            date = new SimpleDateFormat(pattern);
            this.color = color;
        }

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();

            builder.append(date.format(record.getMillis()));
            builder.append(" [");
            builder.append(record.getLevel().getLocalizedName().toUpperCase());
            builder.append("] ");
            if (color) {
                builder.append(colorize(formatMessage(record)));
            } else {
                builder.append(formatMessage(record));
            }
            builder.append('\n');

            if (record.getThrown() != null) {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer);
            }

            return builder.toString();
        }
    }

    protected String colorize(String string) {
        if (string.indexOf(ChatColor.COLOR_CHAR) < 0) {
            return string; // no colors in the message
        }
        for (ChatColor color : ChatColor.values()) {
            if (this.color && replacements.containsKey(color)) {
                string = string.replaceAll("(?i)" + color, replacements.get(color));
            } else {
                string = string.replaceAll("(?i)" + color, "");
            }
        }
        return string;
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
                    reader.getTerminal().writer().println(colorize("====" + ChatColor.GOLD + "g>" + ChatColor.RESET + '"' + command + '"'));
                    if (command.startsWith("$")) {
                        server.getScheduler().runTask(null, new EvalTask(command.substring(1), command.startsWith("$$")));
                    } else if (command.startsWith("!")) {
                        server.getScheduler().runTask(null, new ConsoleTask(command.substring(1)));
                    } else {
                        server.getScheduler().runTask(null, new CommandTask(command));
                    }
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

    private class ConsoleTask implements Runnable {
        private String command;

        ConsoleTask(String command) {
            this.command = command;
        }

        @Override
        public void run() {
            switch (command) {
                case "bind":
                    // run keybind code
                    break;
                case "config":
                    // run config code
                    break;
                default:
                    // nothing
            }
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
