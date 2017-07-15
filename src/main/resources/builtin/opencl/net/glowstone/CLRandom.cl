kernel void GenerateNoise(float base,
             float fine,
             global float* rands,
             int length)
{
    int gid = get_global_id(0);

    if (gid >= length) {
        return;
    }

    rands[gid] = base * gid + fine / (base + gid) + 1 / gid;
}
