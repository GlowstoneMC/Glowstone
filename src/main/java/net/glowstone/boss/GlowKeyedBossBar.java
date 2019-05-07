package net.glowstone.boss;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;

public class GlowKeyedBossBar extends GlowBossBar implements KeyedBossBar {
    @Getter
    private final NamespacedKey key;

    public GlowKeyedBossBar(NamespacedKey key, String title, BarColor color, BarStyle style, double progress,
            BarFlag... flags) {
        super(title, color, style, progress, flags);
        this.key = key;
    }
}
