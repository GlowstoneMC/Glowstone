package net.glowstone;

import org.bukkit.ChatColor;
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
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

/**
 * A meta-class to handle all logging and input-related console improvements.
 * Portions are heavily based on CraftBukkit.
 */
public final class ConsoleManager {

    private static final Logger logger = Logger.getLogger("");
    private static String CONSOLE_DATE = "HH:mm:ss";
    private static String FILE_DATE = "yyyy/MM/dd HH:mm:ss";

    private static String CONSOLE_PROMPT = "g>";
    private static String RIGHT_PROMPT = null;

    protected final GlowServer server;
    private final Map<ChatColor, String> replacements = new EnumMap<>(ChatColor.class);
    private final ChatColor[] colors = ChatColor.values();

    protected LineReader reader;
    private ConsoleCommandSender sender;

    private boolean running = true;

    public ConsoleManager(GlowServer server) {
        this.server = server;

        // terminal must be initialized before standard streams are changed

        for (Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }

        // add log handler which writes to console
        logger.addHandler(new FancyConsoleHandler());

        try (Terminal terminal = TerminalBuilder.builder()
                    .name("Glowstone")
                    .system(true)
                    .nativeSignals(true)
                    .signalHandler(Terminal.SignalHandler.SIG_IGN)
                    .build()) {
            reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new CommandCompleter())
                    .build();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception initializing terminal", e);
        }

        System.setOut(new PrintStream(new LoggerOutputStream(Level.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(Level.WARNING), true));

        sender = new ColoredCommandSender();
        CONSOLE_DATE = server.getConsoleDateFormat();
        for (Handler handler : logger.getHandlers()) {
            if (handler.getClass() == FancyConsoleHandler.class) {
                handler.setFormatter(new DateOutputFormatter(CONSOLE_DATE, true));
            }
        }
        CONSOLE_PROMPT = new AttributedStringBuilder()
                .style(AttributedStyle.BOLD)
                .style(AttributedStyle.DEFAULT.background(AttributedStyle.YELLOW))
                .append("g>")
                .style(AttributedStyle.DEFAULT)
                .toAnsi(reader.getTerminal());
        RIGHT_PROMPT = new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.background(AttributedStyle.RED))
                .append(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                .append("\n")
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED | AttributedStyle.BRIGHT))
                .append(LocalTime.now().format(new DateTimeFormatterBuilder()
                        .appendValue(HOUR_OF_DAY, 2)
                        .appendLiteral(':')
                        .appendValue(MINUTE_OF_HOUR, 2)
                        .toFormatter()))
                .toAnsi(reader.getTerminal());
    }

    public ConsoleCommandSender getSender() {
        return sender;
    }

    public void start() {
        Thread thread = new ConsoleCommandThread();
        thread.setName("ConsoleCommandThread");
        thread.setDaemon(true);
        thread.start();
    }

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

    public void stop() {
        running = false;
        for (Handler handler : logger.getHandlers()) {
            handler.flush();
            handler.close();
        }
    }

    private String colorize(String string) {
        if (string.indexOf(ChatColor.COLOR_CHAR) < 0) {
            return string;  // no colors in the message
        } else {
            // colorize or strip all colors
            for (ChatColor color : colors) {
                if (replacements.containsKey(color)) {
                    string = string.replaceAll("(?i)" + color, replacements.get(color));
                } else {
                    string = string.replaceAll("(?i)" + color, "");
                }
            }
            return string;
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
        public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            try {
                List<String> completions = server.getScheduler().syncIfNeeded(() -> server.getCommandMap().tabComplete(sender, line.line()));
                if (completions == null) {
                    return;
                }

                completions.forEach(s -> candidates.add(new Candidate(s)));
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Error while tab completing", t);
            }
        }
    }

    private class ConsoleCommandThread extends Thread {
        @Override
        public void run() {
            String command = null;
            while (running) {
                try {
                    command = reader.readLine(CONSOLE_PROMPT, RIGHT_PROMPT, null, null);
                } catch (UserInterruptException e) {
                    logger.log(Level.WARNING, "Exception while executing command: " + command, e);
                } catch (EndOfFileException e) {
                    logger.log(Level.SEVERE, "Error while reading commands", e);
                }

                if (command == null || (command = command.trim()).isEmpty()) {
                    continue;
                }

                server.getScheduler().runTask(null, new CommandTask(reader.getParser().parse(command, 0, Parser.ParseContext.ACCEPT_LINE).line()));
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

     private class FancyConsoleHandler extends ConsoleHandler {
        public FancyConsoleHandler() {
            setFormatter(new DateOutputFormatter(CONSOLE_DATE, true));
            setOutputStream(System.out);
        }

        @Override
        public synchronized void flush() {
            reader.callWidget(LineReader.FRESH_LINE);
            reader.getTerminal().flush();
            super.flush();
            try {
               reader.callWidget(LineReader.REDRAW_LINE);
           } catch (Throwable ex) {
               reader.getBuffer().clear();
            }
            reader.getTerminal().flush();
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
