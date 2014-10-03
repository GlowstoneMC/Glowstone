package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import lombok.Data;
import org.bukkit.util.BlockVector;

@Data
public final class TabCompleteMessage implements Message {

    private final String text;
    private final BlockVector location;

}

