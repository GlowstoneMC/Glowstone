package net.minecraft.util;

import java.util.HashSet;
import java.util.Set;

public class RegistryNamespacedDefaultedByKey extends RegistryNamespaced {

    public RegistryNamespacedDefaultedByKey(Object defaultKey) {
        super();
    }

    public void validateKey() {

    }

    public Set<String> getKeys() {
        return new HashSet<String>();
    }
}
