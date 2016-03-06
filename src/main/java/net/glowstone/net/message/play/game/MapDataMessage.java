package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Data
public final class MapDataMessage implements Message {

    private final int id, scale;
    private final List<Icon> icons;
    private final Section section;

    @RequiredArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class Icon {
        public final int type, facing, x, y;
    }

    @ToString
    @EqualsAndHashCode
    public static class Section {
        public final int width, height, x, y;
        public final byte[] data;

        public Section(int width, int height, int x, int y, byte... data) {
            checkArgument(width * height == data.length, "width * height == data.length");
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.data = data;
        }
    }
}
