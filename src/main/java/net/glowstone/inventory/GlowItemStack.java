package net.glowstone.inventory;

import net.glowstone.util.nbt.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GlowItemStack extends ItemStack {
    private static final long serialVersionUID = 7920525754029137821L;
    private Map<String, Tag> nbtData;

    public GlowItemStack(final int type) {
        this(type, 1, (short) 0, null);
    }

    public GlowItemStack(final int type, final int amount) {
        this(type, amount, (short) 0, null);
    }

    public GlowItemStack(final int type, final int amount, final short damage) {
        this(type, amount, damage, null);
    }

    public GlowItemStack(final int type, final int amount, final short damage, Map<String, Tag> nbtData) {
        super(type, amount, damage);
        this.nbtData = nbtData;
    }

    public GlowItemStack(ItemStack stack) {
        super(stack.getTypeId(), stack.getAmount(), stack.getDurability());
    }

    public void setNbtData(Map<String, Tag> nbtData) {
        this.nbtData = nbtData;
    }

    public Map<String, Tag> getNbtData() {
        return nbtData;
    }
}
