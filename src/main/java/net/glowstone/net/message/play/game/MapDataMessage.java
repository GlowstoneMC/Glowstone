package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;

import java.util.List;

@Data
public final class MapDataMessage implements Message {

    private final int id, scale;
    private final List<Icon> icons;
    private final Section section;

    @RequiredArgsConstructor
    public static class Icon {
        public final int type, facing, x, y;
    }

    public static class Section {
        public final int width, height, x, y;
        public final byte[] data;

        public Section(int width, int height, int x, int y, byte[] data) {
            Validate.isTrue(width * height == data.length, "width * height == data.length");
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.data = data;
        }
    }
}
