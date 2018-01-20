package net.glowstone.boss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.player.BossBarMessage;
import net.glowstone.util.TextMessage;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class GlowBossBar implements BossBar {

    @Getter
    private final UUID uniqueId;
    private final List<BarFlag> flags = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    @Getter
    private String title;
    @Getter
    private BarColor color;
    @Getter
    private BarStyle style;
    @Getter
    private double progress = 1.0;
    @Getter
    private boolean visible = true;

    /**
     * Creates a boss bar.
     *
     * @param title the bar's title
     * @param color the bar's color
     * @param style the bar's style
     * @param progress the initial progress
     * @param flags the flags
     */
    public GlowBossBar(String title, BarColor color, BarStyle style, double progress,
            BarFlag... flags) {
        this.uniqueId = UUID.randomUUID();
        this.title = title;
        this.color = color;
        this.style = style;
        this.progress = progress;
        Collections.addAll(this.flags, flags);
    }

    public GlowBossBar(String title, BarColor color, BarStyle style, BarFlag... flags) {
        this(title, color, style, 1.0, flags);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        if (isVisible()) {
            sendUpdate(new BossBarMessage(getUniqueId(), BossBarMessage.Action.UPDATE_TITLE, new
                    TextMessage(title)));
        }
    }

    @Override
    public void setColor(BarColor color) {
        this.color = color;
        if (isVisible()) {
            sendUpdate(new BossBarMessage(getUniqueId(), BossBarMessage.Action.UPDATE_STYLE,
                    BossBarMessage.Color
                    .fromBarColor(color), BossBarMessage.Division.fromBarStyle(style)));
        }
    }

    @Override
    public void setStyle(BarStyle style) {
        this.style = style;
        if (isVisible()) {
            sendUpdate(new BossBarMessage(getUniqueId(), BossBarMessage.Action.UPDATE_STYLE,
                    BossBarMessage.Color
                    .fromBarColor(color), BossBarMessage.Division.fromBarStyle(style)));
        }
    }

    @Override
    public void removeFlag(BarFlag flag) {
        if (flags.contains(flag)) {
            flags.remove(flag);
            if (isVisible()) {
                sendUpdate(new BossBarMessage(getUniqueId(), BossBarMessage.Action.UPDATE_FLAGS,
                        flagsToByte()));
            }
        }
    }

    @Override
    public void addFlag(BarFlag flag) {
        if (!flags.contains(flag) && flag != null) {
            flags.add(flag);
            if (isVisible()) {
                sendUpdate(new BossBarMessage(getUniqueId(), BossBarMessage.Action.UPDATE_FLAGS,
                        flagsToByte()));
            }
        }
    }

    @Override
    public boolean hasFlag(BarFlag flag) {
        return flags.contains(flag);
    }

    @Override
    public void setProgress(double progress) {
        this.progress = progress;
        if (isVisible()) {
            sendUpdate(new BossBarMessage(getUniqueId(), BossBarMessage.Action.UPDATE_HEALTH,
                    (float) progress));
        }
    }

    @Override
    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            if (isVisible()) {
                sendUpdate(player, createAddAction());
                if (player instanceof GlowPlayer) {
                    ((GlowPlayer) player).addBossBar(this);
                }
            }
        }
    }

    @Override
    public void removePlayer(Player player) {
        if (players.contains(player)) {
            if (isVisible()) {
                sendUpdate(player, createRemoveAction());
                if (player instanceof GlowPlayer) {
                    ((GlowPlayer) player).removeBossBar(this);
                }
            }
            players.remove(player);
        }
    }

    @Override
    public void removeAll() {
        if (isVisible()) {
            sendUpdate(createRemoveAction());
        }
        for (Player player : players) {
            if (player instanceof GlowPlayer) {
                ((GlowPlayer) player).removeBossBar(this);
            }
        }
        players.clear();
    }

    @Override
    public List<Player> getPlayers() {
        // TODO: Defensive copy
        return players;
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            if (visible) {
                sendUpdate(createAddAction());
            } else {
                sendUpdate(createRemoveAction());
            }
        }
    }

    @Override
    public void show() {
        setVisible(true);
    }

    @Override
    public void hide() {
        setVisible(false);
    }

    private byte flagsToByte() {
        byte flags = 0;
        if (this.flags.contains(BarFlag.DARKEN_SKY)) {
            flags |= 0x01;
        }
        if (this.flags.contains(BarFlag.PLAY_BOSS_MUSIC)) {
            flags |= 0x02;
        }
        if (this.flags.contains(BarFlag.CREATE_FOG)) {
            flags |= 0x02;
        }
        return flags;
    }

    private BossBarMessage createAddAction() {
        return new BossBarMessage(getUniqueId(), BossBarMessage.Action.ADD, new TextMessage(title),
                (float) progress, BossBarMessage.Color.fromBarColor(color),
                BossBarMessage.Division.fromBarStyle(style), flagsToByte());
    }

    private BossBarMessage createRemoveAction() {
        return new BossBarMessage(getUniqueId(), BossBarMessage.Action.REMOVE);
    }

    private void sendUpdate(BossBarMessage message) {
        for (Player player : getPlayers()) {
            sendUpdate(player, message);
        }
    }

    private void sendUpdate(Player player, BossBarMessage message) {
        if (player == null || !player.isOnline()) {
            return;
        }
        GlowPlayer impl = (GlowPlayer) player;
        impl.getSession().send(message);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == GlowBossBar.class && ((GlowBossBar) obj).getUniqueId() == this
                .getUniqueId();
    }
}
