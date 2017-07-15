#pragma OPENCL EXTENSION cl_khr_fp64 : enable

kernel void Rands(double base,
             double fine,
             global double* rands,
             int length)
{
    int gid = get_global_id(0);

    if (gid >= length) {
        return;
    }

    rands[gid] = base * gid + fine / (base + gid) + 1 / gid;
}