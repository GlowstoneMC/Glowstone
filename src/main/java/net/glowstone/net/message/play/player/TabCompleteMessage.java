package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import org.bukkit.util.BlockVector;

public final class TabCompleteMessage implements Message {

    private final String text;
    private final BlockVector location;

    public TabCompleteMessage(String text) {
        this(text, null);
    }

    public TabCompleteMessage(String text, BlockVector location) {
        this.text = text;
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public BlockVector getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "TabCompleteMessage{" +
                "text='" + text + '\'' +
                ", location=" + location +
                '}';
    }
}

