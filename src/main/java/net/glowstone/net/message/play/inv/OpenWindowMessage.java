package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.glowstone.util.TextMessage;

@Data
@RequiredArgsConstructor
public final class OpenWindowMessage implements Message {

    private final int id;
    private final String type;
    private final TextMessage title;
    private final int slots;
    private final int entityId;

    public OpenWindowMessage(int id, String type, String title, int slots) {
        this(id, type, new TextMessage(title), slots, 0);
    }

}
