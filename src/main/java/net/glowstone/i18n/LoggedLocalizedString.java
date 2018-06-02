package net.glowstone.i18n;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import net.glowstone.GlowServer;

public class LoggedLocalizedString extends LocalizedStringImpl
    implements LoggableLocalizedString, LogRecordableLocalizedString {

    private final Level logLevel;

    private final Logger logger;

    LoggedLocalizedString(String key, Level logLevel) {
        super(key);
        this.logLevel = logLevel;
        this.logger = GlowServer.logger;
    }

    LoggedLocalizedString(String key, Level logLevel,
                                ResourceBundle resourceBundle,
                                Logger logger) {
        super(key, resourceBundle);
        this.logLevel = logLevel;
        this.logger = logger;
    }

    @Override
    public void log() {
        logger.log(logLevel, get());
    }

    @Override
    public void log(Object... args) {
        logger.log(logLevel, get(args));
    }

    @Override
    public void log(Throwable ex) {
        logger.log(logLevel, get(), ex);
    }

    @Override
    public void log(Throwable ex, Object... args) {
        logger.log(logLevel, get(args), ex);
    }

    @Override
    public LogRecord record() {
        return new LogRecord(logLevel, get());
    }

    @Override
    public LogRecord record(Object... args) {
        return new LogRecord(logLevel, get(args));
    }

    @Override
    public LogRecord record(Throwable ex) {
        return new LogRecord(logLevel, get(ex));
    }

    @Override
    public LogRecord record(Throwable ex, Object... args) {
        return new LogRecord(logLevel, get(ex, args));
    }
}
