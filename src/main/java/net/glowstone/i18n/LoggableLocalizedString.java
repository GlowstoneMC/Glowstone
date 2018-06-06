package net.glowstone.i18n;

import java.util.logging.LogRecord;

public interface LoggableLocalizedString extends LocalizedString {
    void log();

    void log(Object... args);

    void log(Throwable ex);

    void log(Throwable ex, Object... args);

    LogRecord record();

    LogRecord record(Object... args);

    LogRecord record(Throwable ex);

    LogRecord record(Throwable ex, Object... args);
}
