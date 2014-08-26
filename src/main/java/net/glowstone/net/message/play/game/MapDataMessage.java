package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import org.apache.commons.lang.Validate;

import java.util.List;

public final class MapDataMessage implements Message {

    private final int id, scale;
    private final List<Icon> icons;
    private final Section section;

    public MapDataMessage(int id, int scale, List<Icon> icons, Section section) {
        this.id = id;
        this.scale = scale;
        this.icons = icons;
        this.section = section;
    }

    public int getId() {
        return id;
    }

    public int getScale() {
        return scale;
    }

    public List<Icon> getIcons() {
        return icons;
    }

    public Section getSection() {
        return section;
    }

    @Override
    public String toString() {
        return "MapDataMessage{" +
                "id=" + id +
                ", scale=" + scale +
                ", icons=" + icons +
                ", section=" + section +
                '}';
    }

    public static class Icon {
        public final int type, facing, x, y;

        public Icon(int type, int facing, int x, int y) {
            this.type = type;
            this.facing = facing;
            this.x = x;
            this.y = y;
        }
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
