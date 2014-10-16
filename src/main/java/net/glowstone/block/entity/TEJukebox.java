package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowJukeboxx;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.inventory.ItemStack;

public class TEJukebox extends TileEntity {

    private ItemStack playing;

    public TEJukebox(GlowBlock block) {
        super(block);
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        this.playing = tag.containsKey("RecordItem") ? NbtSerialization.readItem(tag.getCompound("RecordItem")) : null;
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putCompound("RecordItem", NbtSerialization.writeItem(this.playing, 0));
    }

    @Override
    public GlowBlockState getState() {
        return new GlowJukeboxx(block);
    }

    public ItemStack getPlaying() {
        return playing;
    }

    public void setPlaying(ItemStack playing) {
        this.playing = playing;
    }
}
