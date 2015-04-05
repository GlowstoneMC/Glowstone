package net.minecraft.util;

import java.util.*;

public class RegistryNamespaced {

    public ObjectIntIdentityMap underlyingIntegerMap;
    public List<Object> registryObjects = new ArrayList<>();
    public HashMap<Object, Object> field_148758_b = new HashMap<>();

    public Object getNameForObject(Object object) {
        return null;
    }

    public int getIDForObject(Object thing) {
        return 0;
    }

    public void register(int id, Object name, Object thing) {

    }

    public void putObject(Object objName, Object obj) {

    }

    public Object getObject(Object name) {
        return null;
    }

    public Object getObjectById(int id) {
        return null;
    }

    public boolean containsKey(Object name) {
        return false;
    }

    public Iterator<?> iterator() {
        return registryObjects.iterator();
    }
}
