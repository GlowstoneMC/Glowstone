package net.glowstone.util.pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockPattern {

    private PatternItem[] blocks;

    public BlockPattern(PatternItem... blocks) {
        this.blocks = blocks;
    }

    /**
     * Test whether this pattern matches a block.
     *
     * @param location the base location
     * @param clear    if true, change the matching blocks for one alignment to air
     * @param xz       TODO: document this parameter
     * @param y        TODO: document this parameter
     * @return true if this pattern matches; false otherwise
     */
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

    /**
     * Test whether this pattern matches a block.
     *
     * @param location  the base location
     * @param xz        TODO: document this parameter
     * @param y         TODO: document this parameter
     * @param alignment TODO: document this parameter
     * @return true if this pattern matches; false otherwise
     */
    public Location[] matches(Location location, int xz, int y, Alignment alignment) {
        int i = 0;
        Location[] r = new Location[blocks.length];
        for (PatternItem block : blocks) {
            int dxz = block.xz - xz;
            int dy = block.y - y;
            Location relative = location.clone()
                .add(dxz * alignment.x, -dy, dxz * alignment.z);
            if (relative.getBlock().getType() != block.getType() || (
                (relative.getBlock().getData() != block.getData()) && block.getData() != -1)) {
                return null;
            }
            r[i++] = relative;
        }
        return r;
    }

    private enum Alignment {
        X(1, 0), Z(0, 1);

        private final int x;
        private final int z;

        /**
         * Creates an alignment.
         *
         * @param x the x offset
         * @param z the z offset
         */
        Alignment(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }

    public static class PatternItem {

        private Material type;
        private byte data;
        private int xz;
        private int y;

        /**
         * Creates a PatternItem that fixes a specific block.
         *
         * @param type the block type to match
         * @param data the block data value to match
         * @param xz   TODO: document this parameter
         * @param y    TODO: document this parameter
         */
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

        public int getXz() {
            return xz;
        }

        public int getY() {
            return y;
        }

        public boolean matches(Block block) {
            return block.getType() == getType() && block.getData() == getData();
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
}
