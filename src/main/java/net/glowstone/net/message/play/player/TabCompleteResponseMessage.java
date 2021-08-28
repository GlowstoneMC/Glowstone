package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.util.TextMessage;

import java.util.List;

@Data
public final class TabCompleteResponseMessage implements Message {

    private final int transactionId;
    private final int startIndex;
    private final int textLength;
    private final List<Completion> completions;

    @Data
    public static final class Completion {
        private final String match;
        private final TextMessage tooltip;
    }
}

