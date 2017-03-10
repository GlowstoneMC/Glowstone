package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.util.nbt.CompoundTag;

public class EnchantingTableEntity extends BlockEntity {

    private String name = null; // TODO name the inventory this

    public EnchantingTableEntity(GlowBlock block) {
        super(block);
        setSaveId("minecraft:enchanting_table");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        if (tag.containsKey("CustomName")) {
            name = tag.getString("CustomName");
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        if (name != null) {
            tag.putString("CustomName", name);
        }
    }
}
