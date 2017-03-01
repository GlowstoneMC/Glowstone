package net.glowstone.util;

import com.jogamp.opencl.CLContext;

public class OpenCL {

    private int elementCount;
    private int localSize;
    private int globalSize;
    private CLContext context;

    public OpenCL(int elementCount, int localSize) {
        this.elementCount = elementCount;
        this.localSize = localSize;
        int r = elementCount % localSize;
        globalSize = elementCount;
        if (r != 0) {
            globalSize += localSize - r;
        }

        context = CLContext.create();
    }

    public CLContext getContext() {
        return context;
    }

    public int getGlobalSize() {
        return globalSize;
    }

    public int getLocalSize() {
        return localSize;
    }

    public int getElementCount() {
        return elementCount;
    }
}
