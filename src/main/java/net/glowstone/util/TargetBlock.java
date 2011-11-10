// $Id$
/*
 * Copyright (c) 2011 toi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
*/

package net.glowstone.util;

import gnu.trove.set.hash.TIntHashSet;
import net.glowstone.block.BlockID;
import net.glowstone.entity.GlowLivingEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * This class uses an inefficient method to figure out what block a player
 * is looking towards.
 * 
 * Originally written by toi. It was ported to WorldEdit and trimmed down by
 * sk89q. Thanks to Raphfrk for optimization of toi's original class.
 * Ported to Glowstone by zml2008. This classs has come a long way. Treat it nicely.
 * 
 * @author toi
 */
public class TargetBlock {
    private World world;
    private int maxDistance;
    private double checkDistance, curDistance;
    private Vector targetPos = new Vector();
    private Vector targetPosDouble = new Vector();
    private Vector prevPos = new Vector();
    private Vector offset = new Vector();
    private TIntHashSet transparentBlocks = null;

    /**
     * Constructor requiring a player, uses default values
     * 
     * @param player player to work with
     */
    public TargetBlock(GlowLivingEntity player, TIntHashSet transparent) {
        this.world = player.getWorld();
        this.setValues(player.getLocation(), 300, player.getEyeHeight(), 0.2, transparent);
    }

    /**
     * Constructor requiring a player, max distance and a checking distance
     * 
     * @param player LocalPlayer to work with
     * @param maxDistance how far it checks for blocks
     * @param checkDistance how often to check for blocks, the smaller the more precise
     */
    public TargetBlock(GlowLivingEntity player, int maxDistance, double checkDistance, TIntHashSet transparent) {
        this.world = player.getWorld();
        this.setValues(player.getLocation(),
                maxDistance, player.getEyeHeight(), checkDistance, transparent);
    }

    /**
     * Set the values, all constructors uses this function
     * 
     * @param loc location of the view
     * @param maxDistance how far it checks for blocks
     * @param viewHeight where the view is positioned in y-axis
     * @param checkDistance how often to check for blocks, the smaller the more precise
     */
    private void setValues(Location loc,
            int maxDistance, double viewHeight, double checkDistance, TIntHashSet transparent) {
        this.maxDistance = maxDistance;
        this.checkDistance = checkDistance;
        this.curDistance = 0;
        int xRotation = (int)(loc.getYaw() + 90) % 360;
        int yRotation = (int)loc.getPitch() * -1;

        double h = (checkDistance * Math.cos(Math.toRadians(yRotation)));
        
        offset = new Vector((h * Math.cos(Math.toRadians(xRotation))),
                            (checkDistance * Math.sin(Math.toRadians(yRotation))),
                            (h * Math.sin(Math.toRadians(xRotation))));

        targetPosDouble = loc.clone().add(0, viewHeight, 0).toVector();
        targetPos = targetPosDouble.toBlockVector();
        prevPos = targetPos;
        this.transparentBlocks = transparent;
        if (transparentBlocks == null) {
            transparentBlocks = new TIntHashSet(new int[] {0});
        }
    }

    /**
     * Returns any block at the sight. Returns null if out of range or if no
     * viable target was found. Will try to return the last valid air block it finds.
     * 
     * @return Block
     */
    public Location getAnyTargetBlock() {
        boolean searchForLastBlock = true;
        Location lastBlock = null;
        while (getNextBlock() != null) {
            if (world.getBlockTypeIdAt(getCurrentBlock()) == BlockID.AIR) {
                if(searchForLastBlock) {
                    lastBlock = getCurrentBlock();
                    if (lastBlock.getBlockY() <= 0 || lastBlock.getBlockY() >= world.getMaxHeight() - 1) {
                        searchForLastBlock = false;
                    }
                }
            } else {
                break;
            }
        }
        Location currentBlock = getCurrentBlock();
        return (currentBlock != null ? currentBlock : lastBlock);
    }
    
    /**
     * Returns the block at the sight. Returns null if out of range or if no
     * viable target was found
     * 
     * @return Block
     */
    public Location getTargetBlock() {
        while ((getNextBlock() != null)
                && (world.getBlockTypeIdAt(getCurrentBlock()) == BlockID.AIR));
        return getCurrentBlock();
    }

    /**
     * Returns the block at the sight. Returns null if out of range or if no
     * viable target was found
     * 
     * @return Block
     */
    public Location getSolidTargetBlock() {
        while ((getNextBlock() != null)
                && transparentBlocks.contains(world.getBlockTypeIdAt(getCurrentBlock())));
        return getCurrentBlock();
    }

    /**
     * Get next block
     * 
     * @return next block position
     */
    public Location getNextBlock() {
        prevPos = targetPos;
        do {
            curDistance += checkDistance;
            
            targetPosDouble = offset.clone().add(targetPosDouble);
            targetPos = targetPosDouble.toBlockVector();
        } while (curDistance <= maxDistance
                && targetPos.getBlockX() == prevPos.getBlockX()
                && targetPos.getBlockY() == prevPos.getBlockY()
                && targetPos.getBlockZ() == prevPos.getBlockZ());
        
        if (curDistance > maxDistance) {
            return null;
        }

        return new Location(world, targetPos.getX(), targetPos.getY(), targetPos.getZ());
    }

    /**
     * Returns the current block along the line of vision
     * 
     * @return block position
     */
    public Location getCurrentBlock() {
        if (curDistance > maxDistance) {
            return null;
        } else {
            return new Location(world, targetPos.getX(), targetPos.getY(), targetPos.getZ());
        }
    }

    /**
     * Returns the previous block in the aimed path
     * 
     * @return block position
     */
    public Location getPreviousBlock() {
        return new Location(world, prevPos.getX(), prevPos.getY(), prevPos.getZ());
    }
}