package net.glowstone.util;

import com.jogamp.opencl.*;
import net.glowstone.GlowServer;

import java.io.IOException;
import java.util.HashMap;

public class OpenCL {
    private static CLPlatform platform;
    private static CLContext context;
    private static CLDevice device;
    private static CLCommandQueue queue;
    private static HashMap<String, CLProgram> programs = new HashMap<>();

    public static CLProgram getProgram(String name) {
        if (programs.containsKey(name)) {
            return programs.get(name);
        } else {
            try {
                CLProgram program = context.createProgram(ClassLoader.getSystemClassLoader().getResourceAsStream("/opencl/" + name + ".cl")).build();
                programs.put(name, program);
                return program;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void initContext(CLPlatform platform) {
        OpenCL.platform = platform;
        context = CLContext.create(platform);
        device = context.getMaxFlopsDevice();
        queue = device.createCommandQueue();

        GlowServer.logger.info("OpenCL: Using " + platform + " on device " + device + ".");
    }

    public static CLPlatform getPlatform() {
        return platform;
    }

    public static CLContext getContext() {
        return context;
    }

    public static CLDevice getDevice() {
        return device;
    }

    public static CLCommandQueue getQueue() {
        return queue;
    }
}
