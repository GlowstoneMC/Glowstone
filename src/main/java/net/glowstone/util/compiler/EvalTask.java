package net.glowstone.util.compiler;

import net.glowstone.GlowServer;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class EvalTask implements Runnable {
    private final boolean output;
    private final String command;
    private final String source;
    private byte[] classData;
    private MapClassLoader classLoader;

    public EvalTask(String command, boolean output) {
        this.output = output;
        if (output) {
            this.command = command.substring(1);
        } else {
            this.command = command;
        }
        source = "import org.bukkit.*;\n" +
                "public class REPLShell {\n" +
                (output ? "public static Object run() {\n" +
                        "return " + this.command : "public static void run() {\n" +
                        this.command) +
                "\n}\n}\n";
    }

    @Override
    public void run() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            GlowServer.logger.info("You must run Glowstone with a JDK to use REPL.");
            return;
        }
        classLoader = new MapClassLoader();
        ClassDataFileManager classDataFileManager = new ClassDataFileManager(compiler.getStandardFileManager(null, null, null));

        List<JavaFileObject> compilationUnit = new ArrayList<>();
        compilationUnit.add(new JavaSource(source));

        JavaCompiler.CompilationTask task = compiler.getTask(null, classDataFileManager, null, null, null, compilationUnit);
        task.call();

        try {
            GlowServer.logger.info(command + " -> " + (output ? MethodInvocationUtils.invokeStaticMethod(getCompiledClass(), "run") : "no output"));
        } catch (Exception ignored) {

        }
    }

    public Class<?> getCompiledClass() {
        return classLoader.findClass("REPLShell");
    }

    private class JavaSource extends SimpleJavaFileObject {
        private final String source;

        JavaSource(String source) {
            super(URI.create("string:///REPLShell.java"), Kind.SOURCE);
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
            return defineClass(name, classData, 0, classData.length);
        }
    }

    private class JavaClass extends SimpleJavaFileObject {
        private JavaClass() {
            super(URI.create("string:///REPLShell.java"), Kind.CLASS);
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return new ClassDataOutputStream();
        }
    }

    private class ClassDataFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private ClassDataFileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            return new JavaClass();
        }
    }

    private class ClassDataOutputStream extends OutputStream {
        private final ByteArrayOutputStream bytes;

        private ClassDataOutputStream() {
            bytes = new ByteArrayOutputStream();
        }

        @Override
        public void write(int b) throws IOException {
            bytes.write(b);
        }

        @Override
        public void close() throws IOException {
            classData = bytes.toByteArray();
        }
    }
}
