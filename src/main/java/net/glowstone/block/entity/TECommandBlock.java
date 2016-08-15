package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowCommandBlock;
import net.glowstone.util.nbt.CompoundTag;

public class TECommandBlock extends TileEntity {

    private String command = "";
    private String name;
    private String lastOutput = "";
    private int successCount;
    private boolean trackOutput, powered, auto;

    public TECommandBlock(GlowBlock block) {
        super(block);
        setSaveId("Control");
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public String getLastOutput() {
        return lastOutput;
    }

    public void setLastOutput(String lastOutput) {
        this.lastOutput = lastOutput;
    }

    public boolean isTrackOutput() {
        return trackOutput;
    }

    public void setTrackOutput(boolean trackOutput) {
        this.trackOutput = trackOutput;
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        this.name = tag.isString("CustomName") ? tag.getString("CustomName") : null;
        this.command = tag.isString("Command") ? tag.getString("Command") : "";
        this.successCount = tag.isInt("SuccessCount") ? tag.getInt("SuccessCount") : 0;
        this.lastOutput = tag.isString("LastOutput") ? tag.getString("LastOutput") : "";
        this.trackOutput = tag.isByte("TrackOutput") && tag.getBool("TrackOutput");
        this.powered = tag.isByte("powered") && tag.getBool("powered");
        this.auto = tag.isByte("auto") && tag.getBool("auto");
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        if (name != null) {
            tag.putString("CustomName", name);
        }
        tag.putString("Command", command == null ? "" : command);
        tag.putInt("SuccessCount", successCount);
        tag.putString("LastOutput", lastOutput == null ? "" : lastOutput);
        tag.putBool("TrackOutput", trackOutput);
        tag.putBool("powered", powered);
        tag.putBool("auto", auto);
    }

    @Override
    public GlowBlockState getState() {
        return new GlowCommandBlock(block);
    }
}
