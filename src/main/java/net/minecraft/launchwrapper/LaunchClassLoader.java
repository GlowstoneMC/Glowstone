package net.minecraft.launchwrapper;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;

public class LaunchClassLoader extends URLClassLoader {

    public LaunchClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

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

    public byte[] getClassBytes(String className) throws IOException {
        return null;
    }

    public void addTransformerExclusion(String name) {

    }

    public void addClassLoaderExclusion(String name) {

    }
}
