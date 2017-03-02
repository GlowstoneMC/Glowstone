// OpenCL Lerp function, ported from Bukkit
kernel void Lerp(global const double* x, global const double* y, global double* z, global double* r, int numElements, double amplitude) {
    // get index into global data array
    int iGID = get_global_id(0);

    // stop for loop once we get to limit
    if (iGID >= numElements)  {
        return;
    }

    // lerp from bukkit
    r[iGID] = (y[iGID] + x[iGID] * (z[iGID] - y[iGID])) * amplitude;
}
