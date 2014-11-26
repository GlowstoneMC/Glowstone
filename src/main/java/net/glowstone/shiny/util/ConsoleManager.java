package net.glowstone.shiny.util;

import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.*;

/**
 * Logging management business.
 */
public final class ConsoleManager {

    private static final String CONSOLE_DATE = "HH:mm:ss";

    static {
        Logger logger = Logger.getLogger("");
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        logger.addHandler(new FancyConsoleHandler());
    }

    public static org.slf4j.Logger getLogger() {
        return LoggerFactory.getLogger("");
    }

    private static class FancyConsoleHandler extends ConsoleHandler {
        public FancyConsoleHandler() {
            setFormatter(new DateOutputFormatter(CONSOLE_DATE));
            setOutputStream(System.out);
        }
    }

    private static class DateOutputFormatter extends Formatter {
        private final SimpleDateFormat date;

        public DateOutputFormatter(String pattern) {
            date = new SimpleDateFormat(pattern);
        }

        @Override
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();

            builder.append(date.format(record.getMillis()));
            builder.append(" [");
            builder.append(record.getLevel().getLocalizedName().toUpperCase());
            builder.append("] ");
            if (!record.getLoggerName().isEmpty()) {
                builder.append('[').append(record.getLoggerName()).append("] ");
            }
            builder.append(formatMessage(record));
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
