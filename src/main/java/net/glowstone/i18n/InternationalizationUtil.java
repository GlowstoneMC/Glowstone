package net.glowstone.i18n;

import java.text.Collator;
import java.util.Locale;

/** Utility methods for i18n. */
public enum InternationalizationUtil {
    ;
    public static final Collator CASE_INSENSITIVE = Collator.getInstance(Locale.getDefault());
    static {
        CASE_INSENSITIVE.setStrength(Collator.PRIMARY);
    }
}
