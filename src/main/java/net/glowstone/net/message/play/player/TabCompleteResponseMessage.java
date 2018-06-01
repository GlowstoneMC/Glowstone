package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import java.util.List;
import lombok.Data;

@Data
public final class TabCompleteResponseMessage implements Message {

    private final List<String> completions;

}

