package net.glowstone.block.blocktype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.BlockChangeMessage;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Diode;

/**
 *
 * @author Sam
 */
public class BlockRedstone extends BlockType {

    public BlockRedstone() {
        setDrops(new ItemStack(Material.REDSTONE));
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        return block.getRelative(BlockFace.DOWN).getType().isOccluding();
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState) {
        updatePhysics(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public void updatePhysics(GlowBlock me) {
        super.updatePhysics(me);

        for (BlockFace face : ADJACENT) {
            GlowBlock target = me.getRelative(face);

            switch (target.getType()) {
                case DIODE_BLOCK_ON:
                    Diode diode = (Diode) target.getState().getData();
                    if (face == diode.getFacing().getOppositeFace()) {
                        if (me.getData() != 15) {
                            me.setData((byte) 15);
                        }
                        return;
                    }
                    break;
                case REDSTONE_BLOCK:
                case REDSTONE_TORCH_ON:
                    if (me.getData() != 15) {
                        me.setData((byte) 15);
                    }
                    return;
                default:
                    if (target.getType().isSolid() && target.getRelative(BlockFace.DOWN).getType() == Material.REDSTONE_TORCH_ON) {
                        if (me.getData() != 15) {
                            me.setData((byte) 15);
                        }
                        return;
                    }
                    if (target.getType().isSolid()) {
                        for (BlockFace face2 : SIDES) {
                            GlowBlock target2 = target.getRelative(face2);
                            if (target2.getType() == Material.DIODE_BLOCK_ON
                                    && ((Diode) target2.getState().getData()).getFacing() == target2.getFace(target)) {
                                if (me.getData() != 15) {
                                    me.setData((byte) 15);
                                }
                                return;
                            }
                        }
                    }
            }
        }

        byte power = 0;

        for (BlockFace face : calculateConnections(me)) {

            if (face == BlockFace.DOWN) {
                continue;
            }

            GlowBlock target = me.getRelative(face);
            if (target.getType() != Material.REDSTONE_WIRE) {
                if (!target.getType().isSolid()) {
                    target = target.getRelative(BlockFace.DOWN);
                } else if (!me.getRelative(BlockFace.UP).getType().isSolid()) {
                    target = target.getRelative(BlockFace.UP);
                }

                if (target.getType() != Material.REDSTONE_WIRE) {
                    // There is no redstone wire here..
                    continue;
                }
            }

            if (target.getData() > power) {
                power = (byte) (target.getData() - 1);
            }
        }

        if (power != me.getData()) {
            me.setData(power);
            me.getWorld().requestPulse(me, 1);
        }
    }

    @Override
    public void receivePulse(GlowBlock me) {
        BlockChangeMessage bcmsg = new BlockChangeMessage(me.getX(), me.getY(), me.getZ(), me.getTypeId(), me.getData());
        for (GlowPlayer p : me.getWorld().getRawPlayers()) {
            p.sendBlockChange(bcmsg);
        }
    }

    private static final BlockFace[] ADJACENT = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
    private static final BlockFace[] SIDES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public static List<BlockFace> calculateConnections(GlowBlock block) {
        List<BlockFace> value = new ArrayList<>();
        List<BlockFace> connections = new ArrayList<>();
        value.add(BlockFace.DOWN);
        for (BlockFace face : SIDES) {
            GlowBlock target = block.getRelative(face);
            switch (target.getType()) {
                case DIODE_BLOCK_ON:
                case DIODE_BLOCK_OFF:
                    Diode diode = (Diode) target.getState().getData();
                    if (face == diode.getFacing() || face == diode.getFacing().getOppositeFace()) {
                        connections.add(face);
                    }
                    break;
                case REDSTONE_BLOCK:
                case REDSTONE_TORCH_ON:
                case REDSTONE_TORCH_OFF:
                case REDSTONE_WIRE:
                    connections.add(face);
                    break;
                default:
                    if (target.getType().isSolid() && !block.getRelative(BlockFace.UP).getType().isSolid()
                            && target.getRelative(BlockFace.UP).getType() == Material.REDSTONE_WIRE) {
                        connections.add(face);
                    } else if (!target.getType().isSolid()
                            && target.getRelative(BlockFace.DOWN).getType() == Material.REDSTONE_WIRE) {
                        connections.add(face);
                    }
                    break;
            }
        }

        if (connections.isEmpty()) {
            value.addAll(Arrays.asList(SIDES));
        } else {
            value.addAll(connections);
            if (connections.size() == 1) {
                value.add(connections.get(0).getOppositeFace());
            }
        }

        return value;
    }

}
