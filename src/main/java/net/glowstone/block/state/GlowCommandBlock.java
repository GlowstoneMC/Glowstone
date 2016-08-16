package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TECommandBlock;
import org.bukkit.Server;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandException;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class GlowCommandBlock extends GlowBlockState implements CommandBlock {

    private String command = "";
    private String lastOutput = "";
    private String name;
    private int successCount;
    private boolean trackOutput, powered, auto;

    public GlowCommandBlock(GlowBlock block) {
        super(block);
        this.name = getTileEntity().getName();
        this.command = getTileEntity().getCommand();
        this.trackOutput = getTileEntity().isTrackOutput();
        this.successCount = getTileEntity().getSuccessCount();
        this.powered = getTileEntity().isPowered();
        this.auto = getTileEntity().isAuto();
        this.lastOutput = getTileEntity().getLastOutput();
        if (name == null) {
            name = "@";
        }
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
    public void sendMessage(String message) {
        if (trackOutput) {
            setLastOutput(message);
        }
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public Server getServer() {
        return getBlock().getWorld().getServer();
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

    public void executeCommand() {
        if (command == null || command.replace(" ", "").equals("")) {
            return;
        }
        String cmd = command;
        if (cmd.startsWith("/")) {
            cmd = cmd.substring(1);
        }
        try {
            if (!getServer().dispatchCommand(this, cmd)) {
                return;
            }
        } catch (CommandException e) {
            e.printStackTrace();
            return;
        }
        successCount++;
    }

    public TECommandBlock getTileEntity() {
        return (TECommandBlock) getBlock().getTileEntity();
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
        throw new UnsupportedOperationException("CommandBlocks are always operators.");
    }

    @Override
    public boolean isPermissionSet(String name) {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return true;
    }

    @Override
    public boolean hasPermission(String name) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return true;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        throw new UnsupportedOperationException("CommandBlocks do not support permissions.");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        throw new UnsupportedOperationException("CommandBlocks do not support permissions.");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        throw new UnsupportedOperationException("CommandBlocks do not support permissions.");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        throw new UnsupportedOperationException("CommandBlocks do not support permissions.");
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        throw new UnsupportedOperationException("CommandBlocks do not support permissions.");
    }

    @Override
    public void recalculatePermissions() {
        throw new UnsupportedOperationException("CommandBlocks do not support permissions.");
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        throw new UnsupportedOperationException("CommandBlocks do not support permissions.");
    }
}
