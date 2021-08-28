package net.glowstone.util;

import org.bukkit.NamespacedKey;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class NamespacedKeyTest {

    @Test
    public void testFromStringWithKey() {
        String keyRaw = "minecraft:abc";
        NamespacedKey key = new NamespacedKey(keyRaw.substring(0, keyRaw.indexOf(':')),
            keyRaw.substring(keyRaw.indexOf(':') + 1, keyRaw.length()));
        assertThat(key.toString(), equalTo(keyRaw));
    }

    @Test
    public void testFromStringWithoutKey() {
        String keyRaw = "abc";
        NamespacedKey key = new NamespacedKey(NamespacedKey.MINECRAFT, keyRaw);
        assertThat(key.toString(), equalTo("minecraft:" + keyRaw));
    }
}
