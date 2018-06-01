package net.glowstone.i18n;

public interface LoggableLocalizedString extends LocalizedString {
    void log();

    void log(Object... args);

    void log(Throwable ex);

    void log(Throwable ex, Object... args);
}
