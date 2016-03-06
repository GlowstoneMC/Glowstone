package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEJukebox;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class GlowJukebox extends GlowBlockState implements Jukebox {

    private ItemStack playing;

    public GlowJukebox(GlowBlock block) {
        super(block);
        if (block.getType() != Material.JUKEBOX) {
            throw new IllegalArgumentException("GlowNoteBlock: expected JUKEBOX, got " + block.getType());
        }
        playing = getTileEntity().getPlaying();
    }

    private TEJukebox getTileEntity() {
        return (TEJukebox) getBlock().getTileEntity();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            getTileEntity().setPlaying(playing);
        }
        return result;
    }

    public ItemStack getPlayingItem() {
        return playing;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Implementation

    @Override
    public Material getPlaying() {
        return playing.getType();
    }

    @Override
    public boolean isPlaying() {
        return getRawData() == 1;
    }

    @Override
    public void setPlaying(Material record) {
        int id = 0;
        if (record == null || record == Material.AIR) {
            playing = null;
        } else {
            playing = new ItemStack(record);
            id = record.getId();
        }
        Collection<GlowPlayer> players = getWorld().getRawPlayers();
        for (GlowPlayer player : players) {
            player.playEffect(getLocation(), Effect.RECORD_PLAY, id);
        }
        setRawData((byte) (id > 0 ? 1 : 0));
    }

    @Override
    public boolean eject() {
        if (isPlaying()) {
            getWorld().dropItemNaturally(getLocation(), playing);
            setPlaying(null);
            return true;
        }
        return false;
    }

}
