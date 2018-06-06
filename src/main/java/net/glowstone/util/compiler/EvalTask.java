package net.glowstone.util.compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import net.glowstone.util.ReflectionProcessor;
import org.bukkit.ChatColor;

/**
 * A task for which a source string must be compiled and ran to complete.
 */
public class EvalTask implements Runnable {

    private final boolean output;
    private final String source;
    private static Object lastOutput = null;
    final Map<String, byte[]> classData = new HashMap<>();
    private MapClassLoader classLoader;
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    /**
     * Create a new EvalTask.
     *
     * @param command The statement to evaluate.
     * @param output Whether the statement returns an object.
     */
    public EvalTask(String command, boolean output) {
        this.output = output;
        String evaluatedCommand;
        if (output) {
            evaluatedCommand = command.substring(1);
        } else {
            evaluatedCommand = command;
        }
        if (compiler == null) {
            GlowServer.logger.info("Glowstone is not running with a JDK and REPL is falling"
                + "back to basic parsing using reflection.");
            source = evaluatedCommand;
        } else {
            source = "import org.bukkit.*;\n"
                + "import net.glowstone.*;\n"
                + "public class REPLShell {\n"
                + (output ? "public static Object run(Object last) {\n"
                    : "public static void run(Object last) {\n") + evaluatedCommand + "\n}\n}\n";
        }
    }

    /**
     * Compiles and runs the source associated with this evaluation task.
     */
    @Override
    public void run() {
        if (compiler == null) {
            ReflectionProcessor processor = new ReflectionProcessor(source,
                ServerProvider.getServer());
            Object result = processor.process();
            GlowServer.logger.info(
                ChatColor.GOLD + "Eval returned: " + (result == null ? ChatColor.RED
                    + "<no value>"
                    : ChatColor.AQUA + result.toString()));
            return;
        }
        classLoader = new MapClassLoader();
        ClassDataFileManager classDataFileManager = new ClassDataFileManager(compiler
            .getStandardFileManager(null, null, null));

        List<JavaFileObject> compilationUnit = new ArrayList<>();
        compilationUnit.add(new JavaSource("REPLShell", source));

        JavaCompiler.CompilationTask task = compiler.getTask(null, classDataFileManager,
            null, null, null, compilationUnit);
        if (task.call()) {
            try {
                Object returned = MethodInvocationUtils
                    .invokeStaticMethod(getCompiledClass("REPLShell"), "run",
                        lastOutput);
                if (output) {
                    lastOutput = returned;
                    GlowServer.logger.info(" -> " + returned);
                } else {
                    GlowServer.logger.info(" -> <no value>");
                }
            } catch (Exception e) {
                GlowServer.logger.log(Level.SEVERE, "Error in running REPL shell! ", e);
            }
        }
    }

    /**
     * Find a class compiled in this tasks' class loader.
     *
     * @param name The name of the class to find.
     * @return The class.
     */
    public Class<?> getCompiledClass(String name) {
        return classLoader.findClass(name);
    }

    private class JavaSource extends SimpleJavaFileObject {

        private final String source;

        JavaSource(String name, String source) {
            super(URI.create("string:///" + name + ".java"), Kind.SOURCE);
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return source;
        }
    }

    private class MapClassLoader extends ClassLoader {

        @Override
        protected Class<?> findClass(String name) {
            return defineClass(name, classData.get(name), 0, classData.get(name).length);
        }
    }

    public class JavaClass extends SimpleJavaFileObject {

        private final String name;

        public JavaClass(String name) {
            super(URI.create("string:///" + name + ".java"), Kind.CLASS);
            this.name = name;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return new ClassDataOutputStream(name);
        }
    }

    public class ClassDataFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

        public ClassDataFileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className,
            JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            return new JavaClass(className);
        }
    }

    public class ClassDataOutputStream extends OutputStream {

        private final String name;
        private final ByteArrayOutputStream bytes;

        public ClassDataOutputStream(String name) {
            this.name = name;
            bytes = new ByteArrayOutputStream();
        }

        @Override
        public void write(int b) throws IOException {
            bytes.write(b);
        }

        @Override
        public void close() throws IOException {
            classData.put(name, bytes.toByteArray());
        }
    }
}
