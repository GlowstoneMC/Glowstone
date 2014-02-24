package net.glowstone.net.message.play.inv;

import com.flowpowered.networking.Message;

public final class EnchantItemMessage implements Message {

    private final int window, enchantment;
    
    public EnchantItemMessage(int window, int enchantment) {
        this.window = window;
        this.enchantment = enchantment;
    }

    public int getWindow() {
        return window;
    }

    public int getEnchantment() {
        return enchantment;
    }

    @Override
    public String toString() {
        return "EnchantItemMessage{window=" + window + ",enchantment=" + enchantment + "}";
    }
}
