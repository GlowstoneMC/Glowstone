package net.glowstone.boss;

import net.glowstone.GlowServer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BossBarManager {

    private static BossBarManager instance;

    private final List<GlowBossBar> bossBars = new ArrayList<>();
    private final GlowServer server;

    public BossBarManager(GlowServer server) {
        if (instance != null) {
            throw new RuntimeException("BossBar Manager has already been initialized.");
        }
        this.server = server;
        instance = this;
    }

    public List<GlowBossBar> getBossBars() {
        return bossBars;
    }

    public void register(GlowBossBar bossBar) {
        if (bossBars.contains(bossBar)) {
            return;
        }
        bossBars.add(bossBar);
    }

    public void unregister(GlowBossBar bossBar) {
        bossBar.removeAll();
        bossBars.remove(bossBar);
    }

    public void clearBossBars(Player player) {
        for (GlowBossBar bossBar : bossBars) {
            bossBar.removePlayer(player);
        }
    }

    public GlowBossBar getBossBar(UUID uuid) {
        for (GlowBossBar bossBar : bossBars) {
            if (bossBar.getUniqueId() == uuid) {
                return bossBar;
            }
        }
        return null;
    }

    public static BossBarManager getInstance() {
        return instance;
    }
}
