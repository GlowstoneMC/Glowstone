package net.glowstone;

import net.glowstone.util.LibraryManager;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;

public class GlowBootstrap {
    /**
     * Creates a new server on TCP port 25565 and starts listening for connections.
     *
     * @param args The command-line arguments.
     */
    public static void main(String... args) {
        LibraryManager libraryManager = new LibraryManager();
        JarClassLoader libraryClassLoader = libraryManager.run();

        if (libraryClassLoader != null) {
            JclObjectFactory objectFactory = JclObjectFactory.getInstance();
            Object main = objectFactory.create(libraryClassLoader, "net.glowstone.GlowMain", (Object) args);
            Thread mainThread = new Thread((Runnable) main);
            mainThread.setContextClassLoader(libraryClassLoader);
            mainThread.start();
        }
    }


}
