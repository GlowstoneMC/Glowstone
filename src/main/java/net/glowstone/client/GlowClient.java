package net.glowstone.client;

import net.glowstone.GlowServer;
import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.util.lang.I;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.UUID;

public class GlowClient {

    /**
     * The server instance backing this client.
     */
    private GlowServer server;
    /**
     * The version of the client module.
     */
    private static final String VERSION = "0.0.1a";
    /**
     * The window handle.
     */
    private long window;
    /**
     * The faked human entity. Subject to change in the future as a networking system is added.
     */
    private GlowClientEntity entity;

    public GlowClient(GlowServer server) {
        this.server = server;
    }

    public void run() {
        GlowServer.logger.info(I.tr("status.client.loaded", VERSION));

        start();
        pulse();

        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    public void start() {
        GLFWErrorCallback.createPrint(System.err).set();

        entity = new GlowClientEntity(server.getWorlds().get(0).getSpawnLocation(), new PlayerProfile("GlowClient", UUID.randomUUID()));

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(1280, 720, "GlowClient", MemoryUtil.NULL, MemoryUtil.NULL);

        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create window");
        }

        GLFW.glfwSetKeyCallback(window, this::onInput);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            GLFW.glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
    }

    public void pulse() {
        GL.createCapabilities();

        GL11.glClearColor(247, 147, 29, 0);

        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            GLFW.glfwSwapBuffers(window);

            GLFW.glfwPollEvents();
        }
    }

    public void onInput(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
            GLFW.glfwSetWindowShouldClose(window, true);
        }
    }
}
