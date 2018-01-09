package net.glowstone.util;

import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

public class ClassPathAgent {

    private static Instrumentation inst = null;

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        inst = instrumentation;
    }

    public static void addJarFile(JarFile file) {
        if (inst != null) {
            inst.appendToSystemClassLoaderSearch(file);
        }
    }
}
