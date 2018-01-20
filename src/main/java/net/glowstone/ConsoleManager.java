package net.glowstone;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import lombok.Getter;
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
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

/**
 * A meta-class to handle all logging and input-related console improvements. Portions are heavily
 * based on CraftBukkit.
 */
public final class ConsoleManager {

    private static final Logger logger = Logger.getLogger("");
    private static String CONSOLE_DATE = "HH:mm:ss";
    private static String FILE_DATE = "yyyy/MM/dd HH:mm:ss";
    private static String CONSOLE_PROMPT = ">";
    private final GlowServer server;
    private final Map<ChatColor, String> replacements = new EnumMap<>(ChatColor.class);
    private final ChatColor[] colors = ChatColor.values();

    private ConsoleReader reader;
    /**
     * Returns this ConsoleManager's console as a ConsoleCommandSender.
     *
     * @return the ConsoleCommandSender instance for this ConsoleManager's console
     */
    @Getter
    private ConsoleCommandSender sender;

    private boolean running = true;
    private boolean jline;

    /**
     * Creates the instance for the given server.
     *
     * @param server the server
     */
    public ConsoleManager(GlowServer server) {
        this.server = server;

        // install Ansi code handler, which makes colors work on Windows
        AnsiConsole.systemInstall();

        for (Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }

        // add log handler which writes to console
        logger.addHandler(new FancyConsoleHandler());

        // reader must be initialized before standard streams are changed
        try {
            reader = new ConsoleReader();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Exception initializing console reader", ex);
        }
        reader.addCompleter(new CommandCompleter());

        // set system output streams
        System.setOut(new PrintStream(new LoggerOutputStream(Level.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(Level.WARNING), true));

        // set up colorization replacements
        replacements.put(ChatColor.BLACK,
                Ansi.ansi().a(Attribute.RESET).fg(Color.BLACK).boldOff().toString());
        replacements.put(ChatColor.DARK_BLUE,
                Ansi.ansi().a(Attribute.RESET).fg(Color.BLUE).boldOff().toString());
        replacements.put(ChatColor.DARK_GREEN,
                Ansi.ansi().a(Attribute.RESET).fg(Color.GREEN).boldOff().toString());
        replacements.put(ChatColor.DARK_AQUA,
                Ansi.ansi().a(Attribute.RESET).fg(Color.CYAN).boldOff().toString());
        replacements.put(ChatColor.DARK_RED,
                Ansi.ansi().a(Attribute.RESET).fg(Color.RED).boldOff().toString());
        replacements.put(ChatColor.DARK_PURPLE,
                Ansi.ansi().a(Attribute.RESET).fg(Color.MAGENTA).boldOff().toString());
        replacements.put(ChatColor.GOLD,
                Ansi.ansi().a(Attribute.RESET).fg(Color.YELLOW).boldOff().toString());
        replacements.put(ChatColor.GRAY,
                Ansi.ansi().a(Attribute.RESET).fg(Color.WHITE).boldOff().toString());
        replacements.put(ChatColor.DARK_GRAY,
                Ansi.ansi().a(Attribute.RESET).fg(Color.BLACK).bold().toString());
        replacements
                .put(ChatColor.BLUE, Ansi.ansi().a(Attribute.RESET).fg(Color.BLUE).bold()
                        .toString());
        replacements
                .put(ChatColor.GREEN, Ansi.ansi().a(Attribute.RESET).fg(Color.GREEN).bold()
                        .toString());
        replacements
                .put(ChatColor.AQUA, Ansi.ansi().a(Attribute.RESET).fg(Color.CYAN).bold()
                        .toString());
        replacements
                .put(ChatColor.RED, Ansi.ansi().a(Attribute.RESET).fg(Color.RED).bold().toString());
        replacements.put(ChatColor.LIGHT_PURPLE,
                Ansi.ansi().a(Attribute.RESET).fg(Color.MAGENTA).bold().toString());
        replacements.put(ChatColor.YELLOW,
                Ansi.ansi().a(Attribute.RESET).fg(Color.YELLOW).bold().toString());
        replacements
                .put(ChatColor.WHITE, Ansi.ansi().a(Attribute.RESET).fg(Color.WHITE).bold()
                        .toString());
        replacements.put(ChatColor.MAGIC, Ansi.ansi().a(Attribute.BLINK_SLOW).toString());
        replacements.put(ChatColor.BOLD, Ansi.ansi().a(Attribute.UNDERLINE_DOUBLE).toString());
        replacements
                .put(ChatColor.STRIKETHROUGH, Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString());
        replacements.put(ChatColor.UNDERLINE, Ansi.ansi().a(Attribute.UNDERLINE).toString());
        replacements.put(ChatColor.ITALIC, Ansi.ansi().a(Attribute.ITALIC).toString());
        replacements.put(ChatColor.RESET, Ansi.ansi().a(Attribute.RESET).toString());
    }

    /**
     * Starts the console.
     *
     * @param jline TODO: document this parameter
     */
    public void startConsole(boolean jline) {
        this.jline = jline;

        sender = new ColoredCommandSender();
        CONSOLE_DATE = server.getConsoleDateFormat();
        for (Handler handler : logger.getHandlers()) {
            if (handler.getClass() == FancyConsoleHandler.class) {
                handler.setFormatter(new DateOutputFormatter(CONSOLE_DATE, true));
            }
        }
        CONSOLE_PROMPT = server.getConsolePrompt();
        Thread thread = new ConsoleCommandThread();
        thread.setName("ConsoleCommandThread");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Adds a console-log handler writing to the given file.
     *
     * @param logfile the file path
     */
    public void startFile(String logfile) {
        File parent = new File(logfile).getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
            logger.warning("Could not create log folder: " + parent);
        }
        Handler fileHandler = new RotatingFileHandler(logfile);
        FILE_DATE = server.getConsoleLogDateFormat();
        fileHandler.setFormatter(new DateOutputFormatter(FILE_DATE, false));
        logger.addHandler(fileHandler);
    }

    /**
     * Stops all console-log handlers.
     */
    public void stop() {
        running = false;
        for (Handler handler : logger.getHandlers()) {
            handler.flush();
            handler.close();
        }
    }

    private String colorize(String string) {
        if (string == null || string.indexOf(ChatColor.COLOR_CHAR) < 0) {
            return string;  // no colors in the message
        } else if (!jline || !reader.getTerminal().isAnsiSupported()) {
            return ChatColor.stripColor(string);  // color not supported
        } else {
            // colorize or strip all colors
            for (ChatColor color : colors) {
                if (replacements.containsKey(color)) {
                    string = string.replaceAll("(?i)" + color, replacements.get(color));
                } else {
                    string = string.replaceAll("(?i)" + color, "");
                }
            }
            return string + Ansi.ansi().reset();
        }
    }

    private static class LoggerOutputStream extends ByteArrayOutputStream {

        private final String separator = System.getProperty("line.separator");
        private final Level level;

        public LoggerOutputStream(Level level) {
            this.level = level;
        }

        @Override
        public synchronized void flush() throws IOException {
            super.flush();
            String record = toString();
            reset();

            if (!record.isEmpty() && !record.equals(separator)) {
                logger.logp(level, "LoggerOutputStream", "log" + level, record);
            }
        }
    }

    private static class RotatingFileHandler extends StreamHandler {

        private final SimpleDateFormat dateFormat;
        private final String template;
        private final boolean rotate;
        private String filename;

        public RotatingFileHandler(String template) {
            this.template = template;
            rotate = template.contains("%D");
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            filename = calculateFilename();
            updateOutput();
        }

        private void updateOutput() {
            try {
                setOutputStream(new FileOutputStream(filename, true));
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Unable to open " + filename + " for writing", ex);
            }
        }

        private void checkRotate() {
            if (rotate) {
                String newFilename = calculateFilename();
                if (!filename.equals(newFilename)) {
                    filename = newFilename;
                    // note that the console handler doesn't see this message
                    super.publish(new LogRecord(Level.INFO, "Log rotating to: " + filename));
                    updateOutput();
                }
            }
        }

        private String calculateFilename() {
            return template.replace("%D", dateFormat.format(new Date()));
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
        public int complete(String buffer, int cursor, List<CharSequence> candidates) {
            try {
                List<String> completions = server.getScheduler()
                        .syncIfNeeded(() -> server.getCommandMap().tabComplete(sender, buffer));
                if (completions == null) {
                    return cursor;  // no completions
                }
                candidates.addAll(completions);

                // location to position the cursor at (before autofilling takes place)
                return buffer.lastIndexOf(' ') + 1;
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Error while tab completing", t);
                return cursor;
            }
        }
    }

    private class ConsoleCommandThread extends Thread {

        @Override
        public void run() {
            String command = "";
            while (running) {
                try {
                    if (jline) {
                        command = reader.readLine(CONSOLE_PROMPT, null);
                    } else {
                        command = reader.readLine();
                    }

                    if (command == null || command.trim().isEmpty()) {
                        continue;
                    }

                    server.getScheduler().runTask(null, new CommandTask(command.trim()));
                } catch (CommandException ex) {
                    logger.log(Level.WARNING, "Exception while executing command: " + command, ex);
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error while reading commands", ex);
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
            ServerCommandEvent event = EventFactory
                    .callEvent(new ServerCommandEvent(sender, command));
            if (!event.isCancelled()) {
                server.dispatchCommand(sender, event.getCommand());
            }
        }
    }

    private class ColoredCommandSender implements ConsoleCommandSender {

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
            return "CONSOLE";
        }

        @Override
        public Spigot spigot() {
            return spigot;
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
            throw new UnsupportedOperationException(
                    "Cannot change operator status of server console");
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
    }

    private class FancyConsoleHandler extends ConsoleHandler {

        public FancyConsoleHandler() {
            setFormatter(new DateOutputFormatter(CONSOLE_DATE, true));
            setOutputStream(System.out);
        }

        @Override
        public synchronized void flush() {
            try {
                if (jline) {
                    reader.print(ConsoleReader.RESET_LINE + "");
                    reader.flush();
                    super.flush();
                    try {
                        reader.drawLine();
                    } catch (Throwable ex) {
                        reader.getCursorBuffer().clear();
                    }
                    reader.flush();
                } else {
                    super.flush();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "I/O exception flushing console output", ex);
            }
        }
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
                // StringWriter's close() is trivial
                @SuppressWarnings("resource")
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer);
            }

            return builder.toString();
        }
    }

}
