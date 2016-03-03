package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

import java.util.List;

@Data
public final class TabCompleteResponseMessage implements Message {

    private final List<String> completions;

}

