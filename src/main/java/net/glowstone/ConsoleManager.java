package net.glowstone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Formatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.NullCompletor;
import jline.SimpleCompletor;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.ConsoleCommandSender;

/**
 * A meta-class to handle all logging and input-related console improvements.
 * Portions are heavily based on CraftBukkit.
 */
public class ConsoleManager {
    
    private GlowServer server;
    
    private ConsoleReader reader;
    private ConsoleCommandSender sender;
    private ConsoleCommandThread thread;
    private LoggingConsoleHandler handler;
    
    private boolean running = true;
    private boolean fancy = true;
    
    public ConsoleManager(GlowServer server, boolean fancy) {
        this.server = server;
        this.fancy = fancy;
        
        sender = new ColoredCommandSender();
        thread = new ConsoleCommandThread();
        handler = new LoggingConsoleHandler();
        
        Logger logger = server.getLogger();
        logger.setUseParentHandlers(false);
        handler.setFormatter(new DateOutputFormatter(new SimpleDateFormat()));
        logger.addHandler(handler);
        
        try {
            reader = new ConsoleReader();
        } catch (IOException ex) {
            server.getLogger().log(Level.SEVERE, "Exception inintializing console reader: {0}", ex.getMessage());
            ex.printStackTrace();
        }
        
        Runtime.getRuntime().addShutdownHook(new ServerShutdownThread());
        
        thread.setDaemon(true);
        thread.start();
        
        System.setOut(new PrintStream(new LoggerOutputStream(Level.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(Level.SEVERE), true));
    }
    
    public void stop() {
        running = false;
    }
    
    public void refreshCommands() {
        for (Object c : new ArrayList(reader.getCompletors())) {
            reader.removeCompletor((Completor) c);
        }
        
        Completor[] list = new Completor[] { new SimpleCompletor(server.getAllCommands()), new NullCompletor() };
        reader.addCompletor(new ArgumentCompletor(list));
    }
    
    public String colorize(String string) {
        if (!string.contains("\u00A7")) {
            return string;
        } else if (!fancy || !reader.getTerminal().isANSISupported()) {
            return ChatColor.stripColor(string);
        } else {
            return string.replace(ChatColor.RED.toString(), "\033[1;31m")
                .replace(ChatColor.YELLOW.toString(), "\033[1;33m")
                .replace(ChatColor.GREEN.toString(), "\033[1;32m")
                .replace(ChatColor.AQUA.toString(), "\033[1;36m")
                .replace(ChatColor.BLUE.toString(), "\033[1;34m")
                .replace(ChatColor.LIGHT_PURPLE.toString(), "\033[1;35m")
                .replace(ChatColor.BLACK.toString(), "\033[0;0m")
                .replace(ChatColor.DARK_GRAY.toString(), "\033[1;30m")
                .replace(ChatColor.DARK_RED.toString(), "\033[0;31m")
                .replace(ChatColor.GOLD.toString(), "\033[0;33m")
                .replace(ChatColor.DARK_GREEN.toString(), "\033[0;32m")
                .replace(ChatColor.DARK_AQUA.toString(), "\033[0;36m")
                .replace(ChatColor.DARK_BLUE.toString(), "\033[0;34m")
                .replace(ChatColor.DARK_PURPLE.toString(), "\033[0;35m")
                .replace(ChatColor.GRAY.toString(), "\033[0;37m")
                .replace(ChatColor.WHITE.toString(), "\033[1;37m") +
                "\033[0m";
        }
    }
    
    private class ConsoleCommandThread extends Thread {
        @Override
        public void run() {
            String command;
            while (running) {
                try {
                    if (fancy) {
                        command = reader.readLine(">", null);
                    } else {
                        command = reader.readLine();
                    }
                    
                    if (command == null || command.trim().length() == 0)
                        continue;
                    
                    server.getScheduler().scheduleSyncDelayedTask(null, new CommandTask(command.trim()));
                }
                catch (CommandException ex) {
                    System.out.println("Exception while executing command: " + ex.getMessage());
                    ex.printStackTrace();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private class ServerShutdownThread extends Thread {
        @Override
        public void run() {
            server.stop();
        }
    }
    
    private class CommandTask implements Runnable {
        private String command;
        
        public CommandTask(String command) {
            this.command = command;
        }
        
        public void run() {
            if (!server.dispatchCommand(sender, command)) {
                String firstword = command;
                if (command.indexOf(' ') >= 0) {
                    firstword = command.substring(0, command.indexOf(' '));
                }

                System.out.println("Command not found: " + firstword);
            }
        }
    }
    
    private class ColoredCommandSender extends ConsoleCommandSender {
        public ColoredCommandSender() {
            super(server);
        }
        
        @Override
        public void sendMessage(String text) {
            server.getLogger().info(text);
        }
    }
    
    private class LoggerOutputStream extends ByteArrayOutputStream {
        private final String separator = System.getProperty("line.separator");
        private final Level level;

        public LoggerOutputStream(Level level) {
            super();
            this.level = level;
        }

        @Override
        public synchronized void flush() throws IOException {
            super.flush();
            String record = this.toString();
            super.reset();

            if (record.length() > 0 && !record.equals(separator)) {
                server.getLogger().logp(level, "LoggerOutputStream", "log" + level, record);
            }
        }
    }
    
    private class LoggingConsoleHandler extends ConsoleHandler {
        public LoggingConsoleHandler() {
            //setOutputStream(System.out);
        }
        
        @Override
        public synchronized void flush() {
            try {
                if (fancy) {
                    reader.printString(ConsoleReader.RESET_LINE + "");
                    reader.flushConsole();
                    super.flush();
                    try {
                        reader.drawLine();
                    } catch (Throwable ex) {
                        reader.getCursorBuffer().clearBuffer();
                    }
                    reader.flushConsole();
                } else {
                    super.flush();
                }
            } catch (IOException ex) {
                server.getLogger().severe("I/O exception flushing console output");
                ex.printStackTrace();
            }
        }
    }
    
    private class DateOutputFormatter extends Formatter {
        private SimpleDateFormat date;
        
        public DateOutputFormatter(SimpleDateFormat date) {
            this.date = date;
        }
        
        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();

            builder.append(date.format(record.getMillis()));
            builder.append(" [");
            builder.append(record.getLevel().getLocalizedName().toUpperCase());
            builder.append("] ");
            builder.append(colorize(formatMessage(record)));
            builder.append('\n');

            if (record.getThrown() != null) {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer.toString());
            }
            
            return builder.toString();
        }
    }
    
}