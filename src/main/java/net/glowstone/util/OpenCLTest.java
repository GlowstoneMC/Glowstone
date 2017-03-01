package net.glowstone.util;

import java.io.IOException;

public class OpenCLTest {
    public static void run() {
        OpenCL ocl = new OpenCL(114444777, 256);

        try {
            ocl.getContext().createProgram(OpenCLTest.class.getResourceAsStream("VectorAdd.cl")).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
