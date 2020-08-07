package net.glowstone.util;

import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLPlatform;
import com.jogamp.opencl.CLProgram;
import lombok.Getter;
import net.glowstone.GlowServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;

public class OpenCompute {

    private static final ClassLoader CLASS_LOADER = OpenCompute.class.getClassLoader();
    private static File openCLDir;
    @Getter
    private static CLPlatform platform;
    @Getter
    private static CLContext context;
    @Getter
    private static CLDevice device;
    @Getter
    private static CLCommandQueue queue;
    private static HashMap<String, CLProgram> programs;
    private static HashMap<CLProgram, HashMap<String, CLKernel>> kernels;

    /**
     * Returns an OpenCL program, loading it synchronously if it's not in cache.
     *
     * @param name the program filename
     * @return the OpenCL program, or null if there isn't a valid program with that name
     */
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
                        GlowServer.logger.log(Level.WARNING,
                                "Could not load custom OpenCL program.", ex);
                    }
                } else {
                    try (InputStream input = CLASS_LOADER
                            .getResourceAsStream("builtin/opencl/" + name)) {
                        CLProgram program = context.createProgram(input).build();
                        programs.put(name, program);
                        return program;
                    } catch (IOException ex) {
                        GlowServer.logger.log(Level.WARNING,
                                "Could not load builtin OpenCL program.", ex);
                    }
                }
            }
        }
        return null;
    }

    public static CLKernel getKernel(CLProgram program, String name) {
        return getKernel(program, name, false);
    }

    /**
     * Returns a {@link CLKernel} that is part of the given {@link CLProgram}.
     *
     * @param program the {@link CLProgram} that contains the kernel
     * @param name the name of the kernel
     * @param threaded if true, always create a new {@link CLKernel} instance
     * @return the {@link CLKernel}
     */
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

    /**
     * Initializes the {@link CLContext}, {@link CLDevice} and {@link CLCommandQueue} for the given
     * {@link CLPlatform}.
     *
     * @param platform the {@link CLPlatform} to use
     */
    public static void initContext(CLPlatform platform) {

        openCLDir = new File("opencl");

        if (!openCLDir.isDirectory() && !openCLDir.mkdirs()) {
            GlowServer.logger.severe("Cannot create OpenCL directory");
        }

        programs = new HashMap<>();
        kernels = new HashMap<>();
        OpenCompute.platform = platform;
        context = CLContext.create(platform);
        device = context.getMaxFlopsDevice();
        queue = device.createCommandQueue();

        GlowServer.logger.info("OpenCL: Using " + platform + " on device " + device + ".");
    }

    /**
     * Calculates the number of work groups.
     *
     * @param size the total number of local work units
     * @return the number of work groups
     */
    public static int getGlobalSize(int size) {
        return getGlobalSize(size, getLocalSize());
    }

    /**
     * Calculates the number of work groups.
     *
     * @param size the total number of local work units
     * @param localWorkSize the number of local work units per work group
     * @return the number of work groups
     */
    public static int getGlobalSize(int size, int localWorkSize) {
        int globalSize = size;
        int r = globalSize % localWorkSize;
        if (r != 0) {
            globalSize += localWorkSize - r;
        }
        return globalSize;
    }

    /**
     * Calculates the number of local work units per work group.
     *
     * @return the size of the work groups
     */
    public static int getLocalSize() {
        return Math.min(device.getMaxWorkGroupSize(), 256);
    }

    /**
     * Calculates the number of local work units per work group, applying a specified maximum.
     *
     * @param max the maximum size allowed
     * @return the size of the work groups
     */
    public static int getLocalSize(int max) {
        return Math.min(device.getMaxWorkGroupSize(), max);
    }

    /**
     * Static de-initializer. Clears all references to {@link CLProgram}, {@link CLKernel} and
     * {@link CLContext} instances.
     */
    public static void release() {
        programs.clear();
        programs = null;
        kernels.clear();
        kernels = null;
        context.release();
    }
}
