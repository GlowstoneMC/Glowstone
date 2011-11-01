package net.glowstone.msg;

public class EnchantItemMessage extends Message {

    private final int transaction, enchantment;
    
    public EnchantItemMessage(int transaction, int enchantment) {
        this.transaction = transaction;
        this.enchantment = enchantment;
    }

    public int getTransaction() {
        return transaction;
    }

    public int getEnchantment() {
        return enchantment;
    }

    @Override
    public String toString() {
        return "EnchantItemMessage{transaction=" + transaction + ",enchantment=" + enchantment + "}";
    }
}
