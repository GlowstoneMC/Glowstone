package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import java.util.List;
import lombok.Data;
import net.glowstone.util.TextMessage;

@Data
public final class TabCompleteResponseMessage implements Message {

    private final int transactionId;
    private final int startIndex;
    private final int textLength;
    private final List<Completion> completions;

    @Data
    public final class Completion {
        private final String match;
        private final TextMessage tooltip;
    }
}

