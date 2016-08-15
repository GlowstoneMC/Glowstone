package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TECommandBlock;
import org.bukkit.block.CommandBlock;

public class GlowCommandBlock extends GlowBlockState implements CommandBlock {

    private String command = "";
    private String name;
    private String lastOutput = "";
    private int successCount;
    private boolean trackOutput, powered, auto;

    public GlowCommandBlock(GlowBlock block) {
        super(block);
        this.name = getTileEntity().getName();
        this.command = getTileEntity().getCommand();
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getSuccessCount() {
        return successCount;
    }

    @Override
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    @Override
    public String getLastOutput() {
        return lastOutput;
    }

    @Override
    public void setLastOutput(String lastOutput) {
        this.lastOutput = lastOutput;
    }

    @Override
    public boolean isTrackOutput() {
        return trackOutput;
    }

    @Override
    public void setTrackOutput(boolean trackOutput) {
        this.trackOutput = trackOutput;
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public boolean isAuto() {
        return auto;
    }

    @Override
    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            getTileEntity().setCommand(command);
            getTileEntity().setName(name);
            getTileEntity().setTrackOutput(trackOutput);
            getTileEntity().setAuto(auto);
            getTileEntity().setSuccessCount(successCount);
            getTileEntity().setLastOutput(lastOutput);
            getTileEntity().setPowered(powered);
            getTileEntity().updateInRange();
        }
        return result;
    }

    public TECommandBlock getTileEntity() {
        return (TECommandBlock) getBlock().getTileEntity();
    }
}
