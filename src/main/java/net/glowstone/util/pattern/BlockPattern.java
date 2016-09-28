package net.glowstone.util.pattern;

import net.glowstone.GlowServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockPattern {

    private PatternItem[] blocks;

    public BlockPattern(PatternItem... blocks) {
        this.blocks = blocks;
    }

    public PatternItem[] getBlocks() {
        return blocks;
    }

    public boolean matches(Location location, boolean clear, int xz, int y) {
        for (Alignment alignment : Alignment.values()) {
            Location[] matches = matches(location, xz, y, alignment);
            if (matches != null) {
                if (clear) {
                    for (Location match : matches) {
                        match.getBlock().setType(Material.AIR);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public Location[] matches(Location location, int xz, int y, Alignment alignment) {
        int i = 0;
        Location[] r = new Location[blocks.length];
        for (PatternItem block : blocks) {
            int xzDiff = block.xz - xz;
            int yDiff = block.y - y;
            Location relative = location.clone().add(xzDiff * alignment.x, -yDiff, xzDiff * alignment.z);
            if (relative.getBlock().getType() != block.getType() || ((relative.getBlock().getData() != block.getData()) && block.getData() != -1)) {
                return null;
            }
            r[i++] = relative;
        }
        return r;
    }

    public static class PatternItem {
        private Material type;
        private byte data;
        private int xz, y;

        public PatternItem(Material type, byte data, int xz, int y) {
            this.type = type;
            this.data = data;
            this.xz = xz;
            this.y = y;
        }

        public Material getType() {
            return type;
        }

        public byte getData() {
            return data;
        }

        public int getXZ() {
            return xz;
        }

        public int getY() {
            return y;
        }

        public boolean matches(Block block) {
            boolean b = block.getType() == getType() && block.getData() == getData();
            GlowServer.logger.info(b + " - " + toString() + " vs " + block.getType() + "/" + block.getData());
            return b;
        }

        @Override
        public String toString() {
            return "{xz=" + xz + ",y=" + y + ",type=" + getType() + ",data=" + data + "}";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() != getClass()) {
                return false;
            }
            PatternItem item = (PatternItem) obj;
            return item.xz == xz && item.y == y && item.type == type && item.data == data;
        }
    }

    public enum Alignment {
        X(1, 0), Z(0, 1);

        private final int x;
        private final int z;

        Alignment(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }
}
