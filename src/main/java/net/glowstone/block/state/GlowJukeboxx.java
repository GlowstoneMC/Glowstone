package net.glowstone.block.state;

import java.util.Collection;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEJukebox;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;

public class GlowJukeboxx extends GlowBlockState implements Jukebox {

    private ItemStack playing;

    public GlowJukeboxx(GlowBlock block) {
        super(block);
        if (block.getType() != Material.JUKEBOX) {
            throw new IllegalArgumentException("GlowNoteBlock: expected JUKEBOX, got " + block.getType());
        }
        this.playing = getTileEntity().getPlaying();
    }

    private TEJukebox getTileEntity() {
        return (TEJukebox) getBlock().getTileEntity();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            getTileEntity().setPlaying(this.playing);
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Implementation

    @Override
    public Material getPlaying() {
        return this.playing.getType();
    }

    @Override
    public void setPlaying(Material record) {
        if (record == null || record == Material.AIR) {
            this.playing = null;
            this.setRawData((byte) 0);
        } else {
            this.playing = new ItemStack(record);
            Collection<GlowPlayer> players = getWorld().getRawPlayers();
            for (GlowPlayer player : players) {
                player.playEffect(getLocation(), Effect.RECORD_PLAY, record.getId());
            }
            this.setRawData((byte) 1);
        }
    }

    @Override
    public boolean isPlaying() {
        return getRawData() == 1;
    }

    @Override
    public boolean eject() {
        if (isPlaying()) {
            getWorld().dropItemNaturally(getLocation(), this.playing);
            this.playing = null;
            this.setRawData((byte) 0);
            return true;
        }
        return false;
    }
}
