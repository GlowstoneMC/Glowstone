package net.glowstone.net.message.play.game;

import static com.google.common.base.Preconditions.checkArgument;

import com.flowpowered.network.Message;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
public final class MapDataMessage implements Message {

    private final int id;
    private final int scale;
    private final List<Icon> icons;
    private final Section section;

    @RequiredArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class Icon {

        public final int type;
        public final int facing;
        public final int x;
        public final int y;
    }

    @ToString
    @EqualsAndHashCode
    public static class Section {

        public final int width;
        public final int height;
        public final int x;
        public final int y;
        public final byte[] data;

        /**
         * Creates an instance.
         * @param width the section width
         * @param height the section height
         * @param x the x offset
         * @param y the y offset
         * @param data the data
         */
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
