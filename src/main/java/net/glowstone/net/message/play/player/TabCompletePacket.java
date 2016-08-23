package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.util.BlockVector;

@Data
public final class TabCompletePacket implements Message {

    private final String text;
    private final boolean assumeCommand;
    private final BlockVector location;

}

