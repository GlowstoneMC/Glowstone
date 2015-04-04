package net.minecraft.launchwrapper;

import java.net.URL;
import java.util.List;
import java.util.Set;

public class LaunchClassLoader extends ClassLoader {

    public void addURL(URL url) {

    }

    public URL[] getSources() {
        return new URL[] {};
    }

    public void clearNegativeEntries(Set<String> classList) {

    }

    public void registerTransformer(String name) {
        System.out.println("LaunchClassLoader registering transformer: " + name);
    }

    public List<IClassTransformer> getTransformers() {
        return null;
    }
}
