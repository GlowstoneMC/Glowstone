package net.glowstone.i18n;

import java.util.logging.LogRecord;

public interface LogRecordableLocalizedString extends LoggableLocalizedString {

    LogRecord record();

    LogRecord record(Object... args);

    LogRecord record(Throwable ex);

    LogRecord record(Throwable ex, Object... args);
}
