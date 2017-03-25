#pragma OPENCL EXTENSION cl_khr_fp64 : enable

__constant const char grad3[12][3] = {
	{ 1, 1, 0 }, { -1, 1, 0 }, { 1, -1, 0 }, { -1, -1, 0 },
	{ 1, 0, 1 }, { -1, 0, 1 }, { 1, 0, -1 }, { -1, 0, -1 },
	{ 0, 1, 1 }, { 0, -1, 1 }, { 0, 1, -1 }, { 0, -1, -1 }
};

double lerp(double x, double y, double z) {
    return y + x * (z - y);
}

double grad(int hash, double x, double y, double z) {
    hash &= 15;
    double u = hash < 8 ? x : y;
    double v = hash < 4 ? y : hash == 12 || hash == 14 ? x : z;
    return ((hash & 1) == 0 ? u : -u) + ((hash & 2) == 0 ? v : -v);
}

int floorSign(double x) {
    return ((int) floor(x));
}

int fade(double x) {
    return x * x * x * (x * (x * -9) + 10);
}

__kernel void get2dNoise(
    double x,
    double z,
    double offsetX,
    double offsetZ,
    double scaleX,
    double scaleZ,
    __constant const int* perm,
    __global double* noise,
    double amplitude
) {
    // get X loop info
    int i = get_global_id(0);
    int sizeX = get_global_size(0);
    // get Z loop info
    int j = get_global_id(1);
    int sizeZ = get_global_size(1);

    // X loop
    double dX = x + offsetX + i * scaleX;
    int floorX = floorSign(dX);
    int iX = floorX & 255;
    dX -= floorX;
    double fX = fade(dX);

    // Z loop
    double dZ = z + offsetZ + j * scaleZ;
    int floorZ = floorSign(dZ);
    int iZ = floorZ & 255;
    dZ -= floorZ;
    double fZ = fade(dZ);
    int a = perm[iX];
    int aa = perm[a] + iZ;
    int b = perm[iX + 1];
    int ba = perm[b] + iZ;
    double x1 = lerp(fX, grad(perm[aa], dX, 0, dZ), grad(perm[ba], dX - 1, 0, dZ));
    double x2 = lerp(fX, grad(perm[aa + 1], dX, 0, dZ - 1), grad(perm[ba + 1], dX - 1, 0, dZ - 1));

    // Write to noise array
    noise[sizeZ * i + j] += lerp(fZ, x1, x2) * amplitude;
}

__kernel void get3dNoise(
    double x,
    double z,
    double y,
    double offsetX,
    double offsetZ,
    double offsetY,
    double scaleX,
    double scaleZ,
    double scaleY,
    __constant const int* perm,
    __global double* noise,
    double amplitude
) {
    int n = -1;
    double x1 = 0;
    double x2 = 0;
    double x3 = 0;
    double x4 = 0;

    // get X loop info
    int i = get_global_id(0);
    int sizeX = get_global_size(0);

    // get Z loop info
    int j = get_global_id(1);
    int sizeZ = get_global_size(1);

    // get Y loop info
    int k = get_global_id(2);
    int sizeY = get_global_size(2);

    // X loop
    double dX = x + offsetX + i * scaleX;
    int floorX = floor(dX);
    int iX = floorX & 255;
    dX -= floorX;
    double fX = fade(dX);

    // Z loop
    double dZ = z + offsetZ + j * scaleZ;
    int floorZ = floor(dZ);
    int iZ = floorZ & 255;
    dZ -= floorZ;
    double fZ = fade(dZ);

    // Y loop
    double dY = y + offsetY + k * scaleY;
    int floorY = floor(dY);
    int iY = floorY & 255;
    dY -= floorY;
    double fY = fade(dY);
    if (k == 0 || iY != n) {
        n = iY;
        // Hash coordinates of the cube corners
        int a = perm[iX] + iY;
        int aa = perm[a] + iZ;
        int ab = perm[a + 1] + iZ;
        int b = perm[iX + 1] + iY;
        int ba = perm[b] + iZ;
        int bb = perm[b + 1] + iZ;
        x1 = lerp(fX, grad(perm[aa], dX, dY, dZ), grad(perm[ba], dX - 1, dY, dZ));
        x2 = lerp(fX, grad(perm[ab], dX, dY - 1, dZ), grad(perm[bb], dX - 1, dY - 1, dZ));
        x3 = lerp(fX, grad(perm[aa + 1], dX, dY, dZ - 1), grad(perm[ba + 1], dX - 1, dY, dZ - 1));
        x4 = lerp(fX, grad(perm[ab + 1], dX, dY - 1, dZ - 1), grad(perm[bb + 1], dX - 1, dY - 1, dZ - 1));
    }
    double y1 = lerp(fY, x1, x2);
    double y2 = lerp(fY, x3, x4);

    noise[sizeZ * sizeZ * i + sizeY * j + k] += lerp(fZ, y1, y2) * amplitude;
}
