package net.glowstone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;
import javax.annotation.Nullable;
import lombok.Getter;
import net.glowstone.i18n.ConsoleMessages;
import net.md_5.bungee.api.chat.BaseComponent;
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
import org.jetbrains.annotations.NonNls;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 * Handles all logging and input-related console improvements.
 */
public final class ConsoleManager {

    @NonNls
    private static String CONSOLE_DATE = "HH:mm:ss";
    @NonNls
    private static String FILE_DATE = "yyyy/MM/dd HH:mm:ss";
    @NonNls
    private static String CONSOLE_PROMPT = "> "; // TODO: fix prompt
    private final GlowServer server;
    private static final Map<ChatColor, String> replacements = new EnumMap<>(ChatColor.class);
    private final ChatColor[] colors = ChatColor.values();
    protected LineReader reader;
    private boolean color;
    /**
     * Returns this ConsoleManager's console as a ConsoleCommandSender.
     *
     * @return the ConsoleCommandSender instance for this ConsoleManager's console
     */
    @Getter
    private ConsoleCommandSender sender;

    protected boolean running;

    private ConsoleHandler handler;

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

    private static void addReplacement(ChatColor formatting, @NonNls String ansi) {
        replacements.put(formatting, ansi);
    }

    /**
     * Creates the instance for the given server.
     *
     * @param server the server
     */
    public ConsoleManager(GlowServer server) {
        this.server = server;
        GlowServer.logger.setUseParentHandlers(false);

        try (Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .name("Glowstone") // NON-NLS
                .build()) {
            reader = LineReaderBuilder.builder()
                    .appName("Glowstone") // NON-NLS
                    .terminal(terminal)
                    .completer(new CommandCompleter())
                    .build();
            reader.unsetOpt(LineReader.Option.INSERT_TAB);
            color = !Objects.equals(terminal.getType(), Terminal.TYPE_DUMB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the console.
     */
    public void start() {
        sender = new ColoredCommandSender();
        CONSOLE_DATE = server.getConsoleDateFormat();
        handler = new ConsoleHandler();
        handler.setFormatter(new DateOutputFormatter(CONSOLE_DATE, color));
        GlowServer.logger.addHandler(handler);
        CONSOLE_PROMPT = server.getConsolePrompt();
        if (!running) {
            running = true;
            new ConsoleCommandThread().start();
        }
    }

    /**
     * Stops all console-log handlers.
     */
    public void stop() {
        running = false;
    }

    /**
     * Adds a console-log handler writing to the given file.
     *
     * @param logfile the file path
     */
    public void startFile(String logfile) {
        File parent = new File(logfile).getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
            ConsoleMessages.Warn.Manager.LOG_FOLDER.log(parent);
        }
        Handler fileHandler = new RotatingFileHandler(logfile);
        FILE_DATE = server.getConsoleLogDateFormat();
        fileHandler.setFormatter(new DateOutputFormatter(FILE_DATE, false));
        GlowServer.logger.addHandler(fileHandler);
    }

    private class DateOutputFormatter extends Formatter {

        private final SimpleDateFormat date;
        private final boolean color;

        public DateOutputFormatter(String pattern, boolean color) {
            date = new SimpleDateFormat(pattern);
            this.color = color;
        }

        @Override
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();

            builder.append(date.format(record.getMillis()));
            builder.append(" ["); // NON-NLS
            builder.append(record.getLevel().getLocalizedName().toUpperCase());
            builder.append("] "); // NON-NLS
            if (color) {
                builder.append(colorize(formatMessage(record)));
            } else {
                builder.append(formatMessage(record));
            }
            builder.append('\n');

            if (record.getThrown() != null) {
                try (PrintWriter writer = new PrintWriter(new StringWriter())) {
                    record.getThrown().printStackTrace(writer);
                    builder.append(writer);
                }
            }

            return builder.toString();
        }
    }

    protected String colorize(String string) {
        if (string.indexOf(ChatColor.COLOR_CHAR) < 0) {
            return string; // no colors in the message
        }
        for (ChatColor color : colors) {
            string = string.replaceAll("(?i)" + color, // NON-NLS
                    this.color ? replacements.getOrDefault(color, "") : "");
        }
        return string;
    }

    private static class RotatingFileHandler extends StreamHandler {

        private final SimpleDateFormat dateFormat;
        private final String template;
        private final boolean rotate;
        private String filename;

        public RotatingFileHandler(String template) {
            this.template = template;
            rotate = template.contains("%D"); // NON-NLS
            dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // NON-NLS
            filename = calculateFilename();
            updateOutput();
        }

        private void updateOutput() {
            try {
                setOutputStream(new FileOutputStream(filename, true));
            } catch (IOException ex) {
                ConsoleMessages.Error.Manager.LOG_FILE.log(ex, filename);
            }
        }

        private void checkRotate() {
            if (rotate) {
                String newFilename = calculateFilename();
                if (!filename.equals(newFilename)) {
                    filename = newFilename;
                    // note that the console handler doesn't see this message
                    super.publish(ConsoleMessages.Info.Manager.ROTATE.record(filename));
                    updateOutput();
                }
            }
        }

        private String calculateFilename() {
            return template.replace("%D", dateFormat.format(new Date())); // NON-NLS
        }

        @Override
        public synchronized void publish(LogRecord record) {
            if (!isLoggable(record)) {
                return;
            }
            checkRotate();
            super.publish(record);
            super.flush();
        }

        @Override
        public synchronized void flush() {
            checkRotate();
            super.flush();
        }
    }

    private class CommandCompleter implements Completer {

        @Override
        public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            List<String> completions = null;
            try {
                completions = server.getScheduler().syncIfNeeded(
                    () -> server.getCommandMap().tabComplete(sender, line.line()));
            } catch (Exception e) {
                ConsoleMessages.Warn.Manager.TAB_COMPLETE.log(e);
            }

            if (completions != null) {
                completions.forEach(completion -> candidates.add(new Candidate(completion)));
            }
        }
    }

    private class ConsoleCommandThread extends Thread {

        ConsoleCommandThread() {
            setName("ConsoleCommandThread"); // NON-NLS
            setDaemon(true);
        }

        @Override
        public void run() {
            String command = null;
            while (running) {
                try {
                    if (color) {
                        command = reader.readLine(colorize(ChatColor.RESET.toString())
                                + CONSOLE_PROMPT);
                    } else {
                        command = reader.readLine(CONSOLE_PROMPT);
                    }
                    if (command != null && !(command = command.trim()).isEmpty()) {
                        reader.getTerminal().writer().println(colorize(String.format(
                                "%s====%sg>%s\"%s\"", // NON-NLS
                                ChatColor.RESET, ChatColor.GOLD, ChatColor.RESET, command)));
                        server.getScheduler().runTask(null, new CommandTask(command));
                    }
                } catch (CommandException ex) {
                    ConsoleMessages.Warn.Manager.COMMAND.log(ex, command);
                } catch (Exception ex) {
                    ConsoleMessages.Error.Manager.COMMAND_READ.log(ex);
                }
            }
        }
    }

    private class CommandTask implements Runnable {

        private final String command;

        public CommandTask(String command) {
            this.command = command;
        }

        @Override
        public void run() {
            ServerCommandEvent event = EventFactory.getInstance()
                    .callEvent(new ServerCommandEvent(sender, command));
            if (!event.isCancelled()) {
                server.dispatchCommand(sender, event.getCommand());
            }
        }
    }

    public class ColoredCommandSender implements ConsoleCommandSender {

        private final PermissibleBase perm = new PermissibleBase(this);

        ////////////////////////////////////////////////////////////////////////
        // CommandSender
        private Spigot spigot = new Spigot() {
            @Override
            public void sendMessage(BaseComponent component) {
                ColoredCommandSender.this.sendMessage(component);
            }

            @Override
            public void sendMessage(BaseComponent... components) {
                ColoredCommandSender.this.sendMessage(components);
            }
        };

        @Override
        public String getName() {
            return "CONSOLE";  // NON-NLS
        }

        @Override
        public Spigot spigot() {
            return spigot;
        }

        @Override
        public void sendMessage(String text) {
            GlowServer.logger.info(text);
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
            throw new UnsupportedOperationException(
                    "Cannot change operator status of server console");
        }

        ////////////////////////////////////////////////////////////////////////
        // Permissible

        @Override
        public boolean isPermissionSet(@NonNls String name) {
            return perm.isPermissionSet(name);
        }

        @Override
        public boolean isPermissionSet(Permission perm) {
            return this.perm.isPermissionSet(perm);
        }

        @Override
        public boolean hasPermission(@NonNls String name) {
            return perm.hasPermission(name);
        }

        @Override
        public boolean hasPermission(Permission perm) {
            return this.perm.hasPermission(perm);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin,
                                                  @NonNls String name, boolean value) {
            return perm.addAttachment(plugin, name, value);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin) {
            return perm.addAttachment(plugin);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, @NonNls String name, boolean value,
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
        public void abandonConversation(Conversation conversation,
                                        ConversationAbandonedEvent details) {

        }

        @Override
        public void sendRawMessage(String message) {

        }

        // Additional methods

        /**
         * Returns the line reader for this console. Used to implement some commands.
         *
         * @return the line reader
         */
        public LineReader getLineReader() {
            return reader;
        }

        private LineReader.Option getOption(String name) {
            for (LineReader.Option option : LineReader.Option.values()) {
                if (option.name().equalsIgnoreCase(name)) {
                    return option;
                }
            }
            return null;
        }

        /**
         * Gets the value of an option or variable from the line reader.
         *
         * @param name the name
         * @return the value
         * @see #getLineReaderOption(String)
         * @see LineReader#isSet(LineReader.Option)
         * @see LineReader#getVariable(String)
         */
        @Nullable
        public Object getLineReaderOption(String name) {
            LineReader.Option option = getOption(name);
            Object value;
            if (option == null) {
                value = reader.getVariable(name);
            } else {
                value = reader.isSet(option);
            }
            return value;
        }

        /**
         * Sets the value of an option or variable on the line reader.
         *
         * @param name  the name
         * @param value the new value
         * @see #getLineReaderOption(String)
         * @see LineReader#option(LineReader.Option, boolean)
         * @see LineReader#setVariable(String, Object)
         */
        public void setLineReaderOption(String name, @Nullable Object value) {
            LineReader.Option option = getOption(name);
            if (option != null) {
                if (value instanceof Boolean) {
                    reader.option(option, (Boolean) value);
                } else {
                    throw new IllegalArgumentException(
                            "Option " + option + " must be true or false");
                }
            } else {
                reader.setVariable(name, value);
            }
        }

    }

}
