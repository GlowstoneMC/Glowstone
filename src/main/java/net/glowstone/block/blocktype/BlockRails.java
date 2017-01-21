package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class BlockRails extends BlockNeedsAttached {
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Arrays.asList(new ItemStack(block.getType()));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        GlowBlock block = state.getBlock();
        RailDirection direction = getRailDirection(block);

        state.setRawData((byte) direction.ordinal());
        state.update(true, false);

        updateNeighbors(block, direction);
    }

    private void updateNeighbors(GlowBlock block, RailDirection direction) {
        direction.getNeighborRails(block).forEach(rail -> {
            // check if the rail is stable
            if (isUnstable(rail, block) && !isConnected(rail, block)) {
                rail.setData((byte) getRailDirection(rail).ordinal());
            }
        });
    }

    private static boolean isConnected(GlowBlock rail, GlowBlock neighborRail) {
        return RailDirection.getRailDirection(rail).getNeighborRails(rail).contains(neighborRail)
                && RailDirection.getRailDirection(neighborRail).getNeighborRails(neighborRail).contains(rail);
    }

    private static boolean isUnstable(GlowBlock block, GlowBlock neighborRail) {
        if (!isRailBlock(block)) {
            return false;
        }

        Set<GlowBlock> neighborRails = RailDirection.getRailDirection(block).getNeighborRails(block);

        // equals because blocks at the same location are not the same at the moment.
        // TODO: !rail.equals(neighborRail) -> rail != neighborRail
        return neighborRails.stream().filter(rail -> !rail.equals(neighborRail) && isConnected(block, rail)).count() != 2;
    }

    private static RailDirection getRailDirection(GlowBlock rail) {
        // north - 0, south - 1, east - 2, west - 3, ascending_north - 4, ascending_south - 5, ascending_east - 6, ascending_west - 7
        boolean[] unconnectedRails = new boolean[8];

        GlowBlock north = rail.getRelative(BlockFace.NORTH);
        GlowBlock south = rail.getRelative(BlockFace.SOUTH);
        GlowBlock east = rail.getRelative(BlockFace.EAST);
        GlowBlock west = rail.getRelative(BlockFace.WEST);

        unconnectedRails[0] = isUnstable(north, rail) || isUnstable(north.getRelative(BlockFace.DOWN), rail);
        unconnectedRails[1] = isUnstable(south, rail) || isUnstable(south.getRelative(BlockFace.DOWN), rail);
        unconnectedRails[2] = isUnstable(east, rail) || isUnstable(east.getRelative(BlockFace.DOWN), rail);
        unconnectedRails[3] = isUnstable(west, rail) || isUnstable(west.getRelative(BlockFace.DOWN), rail);
        unconnectedRails[4] = isUnstable(north.getRelative(BlockFace.UP), rail);
        unconnectedRails[5] = isUnstable(south.getRelative(BlockFace.UP), rail);
        unconnectedRails[6] = isUnstable(east.getRelative(BlockFace.UP), rail);
        unconnectedRails[7] = isUnstable(west.getRelative(BlockFace.UP), rail);

        // north && east || ascending_north && ascending_east || north && ascending_east || ascending_north && east
        if (unconnectedRails[0] && unconnectedRails[2] || unconnectedRails[4] && unconnectedRails[6] || unconnectedRails[0] && unconnectedRails[6] || unconnectedRails[4] && unconnectedRails[2]) {
            return RailDirection.NORTH_EAST;
        }

        // north && west || ascending_north && ascending_west || north && ascending_west || ascending_north && west
        if (unconnectedRails[0] && unconnectedRails[3] || unconnectedRails[4] && unconnectedRails[7] || unconnectedRails[0] && unconnectedRails[7] || unconnectedRails[4] && unconnectedRails[3]) {
            return RailDirection.NORTH_WEST;
        }

        // south && west || ascending_south && ascending_west || south && ascending_west || ascending_south && west
        if (unconnectedRails[1] && unconnectedRails[3] || unconnectedRails[5] && unconnectedRails[7] || unconnectedRails[1] && unconnectedRails[7] || unconnectedRails[5] && unconnectedRails[3]) {
            return RailDirection.SOUTH_WEST;
        }

        // south && east || ascending_south && ascending_east || south && ascending_east || ascending_south && east
        if (unconnectedRails[1] && unconnectedRails[2] || unconnectedRails[5] && unconnectedRails[6] || unconnectedRails[1] && unconnectedRails[6] || unconnectedRails[5] && unconnectedRails[2]) {
            return RailDirection.SOUTH_EAST;
        }

        // ascending_north && south || ascending_north
        if (unconnectedRails[4] && unconnectedRails[1] || unconnectedRails[4]) {
            return RailDirection.ASCENDING_NORTH;
        }

        // ascending_south && north || ascending_south
        if (unconnectedRails[5] && unconnectedRails[0] || unconnectedRails[5]) {
            return RailDirection.ASCENDING_SOUTH;
        }

        // ascending_east && west || ascending_east
        if (unconnectedRails[6] && unconnectedRails[3] || unconnectedRails[6]) {
            return RailDirection.ASCENDING_EAST;
        }

        // ascending_west && east || ascending_west
        if (unconnectedRails[7] && unconnectedRails[2] || unconnectedRails[7]) {
            return RailDirection.ASCENDING_WEST;
        }
        // north || south
        if (unconnectedRails[0] || unconnectedRails[1]) {
            return RailDirection.NORTH_SOUTH;
        }

        // east || west
        if (unconnectedRails[2] || unconnectedRails[3]) {
            return RailDirection.EAST_WEST;
        }

        // return current rail direction.
        return RailDirection.getRailDirection(rail);
    }

    private static boolean isRailBlock(GlowBlock block) {
        return block.getType() == Material.RAILS;
    }

    private enum RailDirection {
        NORTH_SOUTH {
            @Override
            public Set<GlowBlock> getNeighborRails(GlowBlock rail) {
                Set<GlowBlock> rails = new HashSet<>();

                GlowBlock north = rail.getRelative(BlockFace.NORTH);
                GlowBlock south = rail.getRelative(BlockFace.SOUTH);

                if (isRailBlock(north)) {
                    rails.add(north);
                }

                if (isRailBlock(south)) {
                    rails.add(south);
                }

                if (rails.size() != 2) {
                    GlowBlock descendingNorth = north.getRelative(BlockFace.DOWN);
                    GlowBlock descendingSouth = south.getRelative(BlockFace.DOWN);

                    if (isRailBlock(descendingNorth)) {
                        rails.add(descendingNorth);
                    }

                    if (isRailBlock(descendingSouth)) {
                        rails.add(descendingSouth);
                    }
                }

                return rails;
            }
        },
        EAST_WEST {
            @Override
            public Set<GlowBlock> getNeighborRails(GlowBlock rail) {
                Set<GlowBlock> rails = new HashSet<>();

                GlowBlock east = rail.getRelative(BlockFace.EAST);
                GlowBlock west = rail.getRelative(BlockFace.WEST);

                if (isRailBlock(east)) {
                    rails.add(east);
                }

                if (isRailBlock(west)) {
                    rails.add(west);
                }

                if (rails.size() != 2) {
                    GlowBlock descendingEast = east.getRelative(BlockFace.DOWN);
                    GlowBlock descendingWest = west.getRelative(BlockFace.DOWN);

                    if (isRailBlock(descendingEast)) {
                        rails.add(descendingEast);
                    }

                    if (isRailBlock(descendingWest)) {
                        rails.add(descendingWest);
                    }
                }

                return rails;
            }
        },
        ASCENDING_EAST {
            @Override
            public Set<GlowBlock> getNeighborRails(GlowBlock rail) {
                Set<GlowBlock> rails = new HashSet<>();

                GlowBlock west = rail.getRelative(BlockFace.WEST);
                GlowBlock ascendingEast = rail.getRelative(BlockFace.EAST).getRelative(BlockFace.UP);
                GlowBlock descendingWest = west.getRelative(BlockFace.DOWN);

                if (isRailBlock(ascendingEast)) {
                    rails.add(ascendingEast);
                }

                if (isRailBlock(descendingWest)) {
                    rails.add(descendingWest);
                }

                if (rails.size() != 2) {
                    if (isRailBlock(west)) {
                        rails.add(west);
                    }
                }

                return rails;
            }
        },
        ASCENDING_WEST {
            @Override
            public Set<GlowBlock> getNeighborRails(GlowBlock rail) {
                Set<GlowBlock> rails = new HashSet<>();

                GlowBlock east = rail.getRelative(BlockFace.EAST);
                GlowBlock ascendingWest = rail.getRelative(BlockFace.WEST).getRelative(BlockFace.UP);
                GlowBlock descendingEast = east.getRelative(BlockFace.DOWN);

                if (isRailBlock(ascendingWest)) {
                    rails.add(ascendingWest);
                }

                if (isRailBlock(descendingEast)) {
                    rails.add(descendingEast);
                }

                if (rails.size() != 2) {
                    if (isRailBlock(east)) {
                        rails.add(east);
                    }
                }

                return rails;
            }
        },
        ASCENDING_NORTH {
            @Override
            public Set<GlowBlock> getNeighborRails(GlowBlock rail) {
                Set<GlowBlock> rails = new HashSet<>();

                GlowBlock south = rail.getRelative(BlockFace.SOUTH);
                GlowBlock ascendingNorth = rail.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP);
                GlowBlock descendingSouth = south.getRelative(BlockFace.DOWN);

                if (isRailBlock(ascendingNorth)) {
                    rails.add(ascendingNorth);
                }

                if (isRailBlock(descendingSouth)) {
                    rails.add(descendingSouth);
                }

                if (rails.size() != 2) {
                    if (isRailBlock(south)) {
                        rails.add(south);
                    }
                }

                return rails;
            }
        },
        ASCENDING_SOUTH {
            @Override
            public Set<GlowBlock> getNeighborRails(GlowBlock rail) {
                Set<GlowBlock> rails = new HashSet<>();

                GlowBlock north = rail.getRelative(BlockFace.NORTH);
                GlowBlock ascendingSouth = rail.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP);
                GlowBlock descendingNorth = north.getRelative(BlockFace.DOWN);

                if (isRailBlock(ascendingSouth)) {
                    rails.add(ascendingSouth);
                }

                if (isRailBlock(descendingNorth)) {
                    rails.add(descendingNorth);
                }

                if (rails.size() != 2) {
                    if (isRailBlock(north)) {
                        rails.add(north);
                    }
                }

                return rails;
            }
        },
        SOUTH_EAST {
            @Override
            public Set<GlowBlock> getNeighborRails(GlowBlock rail) {
                Set<GlowBlock> rails = new HashSet<>();

                GlowBlock south = rail.getRelative(BlockFace.SOUTH);
                GlowBlock east = rail.getRelative(BlockFace.EAST);

                if (isRailBlock(south)) {
                    rails.add(south);
                }

                if (isRailBlock(east)) {
                    rails.add(east);
                }

                if (rails.size() != 2) {
                    GlowBlock descendingSouth = south.getRelative(BlockFace.DOWN);
                    GlowBlock descendingEast = east.getRelative(BlockFace.DOWN);

                    if (isRailBlock(descendingSouth)) {
                        rails.add(descendingSouth);
                    }

                    if (isRailBlock(descendingEast)) {
                        rails.add(descendingEast);
                    }
                }

                return rails;
            }
        },
        SOUTH_WEST {
            @Override
            public Set<GlowBlock> getNeighborRails(GlowBlock rail) {
                Set<GlowBlock> rails = new HashSet<>();

                GlowBlock south = rail.getRelative(BlockFace.SOUTH);
                GlowBlock west = rail.getRelative(BlockFace.WEST);

                if (isRailBlock(south)) {
                    rails.add(south);
                }

                if (isRailBlock(west)) {
                    rails.add(west);
                }

                if (rails.size() != 2) {
                    GlowBlock descendingSouth = south.getRelative(BlockFace.DOWN);
                    GlowBlock descendingWest = west.getRelative(BlockFace.DOWN);

                    if (isRailBlock(descendingSouth)) {
                        rails.add(descendingSouth);
                    }

                    if (isRailBlock(descendingWest)) {
                        rails.add(descendingWest);
                    }
                }

                return rails;
            }
        },
        NORTH_WEST {
            @Override
            public Set<GlowBlock> getNeighborRails(GlowBlock rail) {
                Set<GlowBlock> rails = new HashSet<>();

                GlowBlock north = rail.getRelative(BlockFace.NORTH);
                GlowBlock west = rail.getRelative(BlockFace.WEST);

                if (isRailBlock(north)) {
                    rails.add(north);
                }

                if (isRailBlock(west)) {
                    rails.add(west);
                }

                if (rails.size() != 2) {
                    GlowBlock descendingNorth = north.getRelative(BlockFace.DOWN);
                    GlowBlock descendingWest = west.getRelative(BlockFace.DOWN);

                    if (isRailBlock(descendingNorth)) {
                        rails.add(descendingNorth);
                    }

                    if (isRailBlock(descendingWest)) {
                        rails.add(descendingWest);
                    }
                }

                return rails;
            }
        },
        NORTH_EAST {
            @Override
            public Set<GlowBlock> getNeighborRails(GlowBlock rail) {
                Set<GlowBlock> rails = new HashSet<>();

                GlowBlock north = rail.getRelative(BlockFace.NORTH);
                GlowBlock east = rail.getRelative(BlockFace.EAST);

                if (isRailBlock(north)) {
                    rails.add(north);
                }

                if (isRailBlock(east)) {
                    rails.add(east);
                }

                if (rails.size() != 2) {
                    GlowBlock descendingNorth = north.getRelative(BlockFace.DOWN);
                    GlowBlock descendingEast = east.getRelative(BlockFace.DOWN);

                    if (isRailBlock(descendingNorth)) {
                        rails.add(descendingNorth);
                    }

                    if (isRailBlock(descendingEast)) {
                        rails.add(descendingEast);
                    }
                }

                return rails;
            }
        };

        private static final Map<Byte, RailDirection> dataValues = new HashMap<>();

        static {
            for (RailDirection direction : RailDirection.values()) {
                dataValues.put((byte) direction.ordinal(), direction);
            }
        }

        public abstract Set<GlowBlock> getNeighborRails(GlowBlock rail);

        public static RailDirection getRailDirection(GlowBlock rail) {
            return dataValues.get(rail.getData());
        }
    }
}
