package net.glowstone.util;

import com.jogamp.opencl.*;
import net.glowstone.GlowServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;

public class OpenCL {
    private static File openCLDir;
    private static CLPlatform platform;
    private static CLContext context;
    private static CLDevice device;
    private static CLCommandQueue queue;
    private static HashMap<String, CLProgram> programs;
    private static HashMap<CLProgram, HashMap<String, CLKernel>> kernels;

    public static CLProgram getProgram(String name) {
        if (programs.containsKey(name)) {
            return programs.get(name);
        } else {
            if (openCLDir.exists() && openCLDir.isDirectory()) {
                File file = new File(openCLDir, name);
                if (file.exists()) {
                    try (InputStream input = new FileInputStream(file)) {
                        CLProgram program = context.createProgram(input).build();
                        programs.put(name, program);
                        return program;
                    } catch (IOException ex) {
                        GlowServer.logger.log(Level.WARNING, "Could not load custom OpenCL program. Trying builtins.", ex);
                    }
                } else {
                    try (InputStream input = OpenCL.class.getClassLoader().getResourceAsStream("builtin/opencl/" + name)) {
                        CLProgram program = context.createProgram(input).build();
                        programs.put(name, program);
                        return program;
                    } catch (IOException ex) {
                        GlowServer.logger.log(Level.WARNING, "Could not load builtin OpenCL program.", ex);
                    }
                }
            }
        }
        return null;
    }

    public static CLKernel getKernel(CLProgram program, String name) {
        return getKernel(program, name, false);
    }

    public static CLKernel getKernel(CLProgram program, String name, boolean threaded) {
        if (kernels.containsKey(program)) {
            HashMap<String, CLKernel> kernel = kernels.get(program);
            if (kernel.containsKey(name) && !threaded) {
                return kernel.get(name);
            } else {
                CLKernel clKernel = program.createCLKernel(name);
                kernel.put(name, clKernel);
                return clKernel;
            }
        } else {
            kernels.put(program, new HashMap<>());
            CLKernel clKernel = program.createCLKernel(name);
            kernels.get(program).put(name, clKernel);
            return clKernel;
        }
    }

    public static void initContext(CLPlatform platform) {

        openCLDir = new File("opencl");

        if (!openCLDir.isDirectory() && !openCLDir.mkdirs()) {
            GlowServer.logger.severe("Cannot create OpenCL directory");
        }

        programs = new HashMap<>();
        kernels = new HashMap<>();
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

    public static int getGlobalSize(int size) {
        int globalSize = size;
        int localWorkSize = getLocalSize();
        int r = globalSize % localWorkSize;
        if (r != 0) {
            globalSize += localWorkSize - r;
        }
        return globalSize;
    }

    public static int getGlobalSize(int size, int localWorkSize) {
        int globalSize = size;
        int r = globalSize % localWorkSize;
        if (r != 0) {
            globalSize += localWorkSize - r;
        }
        return globalSize;
    }

    public static int getLocalSize() {
        return Math.min(device.getMaxWorkGroupSize(), 256);
    }

    public static int getLocalSize(int max) {
        return Math.min(device.getMaxWorkGroupSize(), max);
    }

    public static void release() {
        programs.clear();
        programs = null;
        kernels.clear();
        kernels = null;
        context.release();
    }
}
