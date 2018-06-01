package net.glowstone.util;

import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

public class ClassPathAgent {

    private static Instrumentation inst = null;

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        inst = instrumentation;
    }

    /**
     * Adds a JAR file to the system class loader.
     * 
     * @param file The JAR file to add to the class loader.
     */
    public static void addJarFile(JarFile file) {
        if (inst != null) {
            inst.appendToSystemClassLoaderSearch(file);
        }
    }
}
