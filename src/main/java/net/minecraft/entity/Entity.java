/**
 * Copyright (c) 2013, agaricus. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * The name of the author may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.minecraft.entity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Entity {

    /** entity id */
    public int field_70157_k;

    /** entity position x,y,z */
    public double serverPosX;
    public double serverPosY;
    public double serverPosZ;

    /** entity raw x,y,z */
    public int posX;
    public int posY;
    public int posZ;

    /** entity yaw */
    public float rotationYaw;
    public float rotationYawHead;

    /** entity pitch */
    public float rotationPitch;

    /** head yaw */
    public float field_70759_as;

    /** velocity mX, mY, mZ */
    public double motionX;
    public double motionY;
    public double motionZ;

    public Entity func_70096_w() { /** TODO return type */
        return null;
    }

    /** TODO: unknown class */
    public void func_75689_a(DataOutputStream dataOutputStream) throws IOException {
        throw new IOException();
    }

    public void setLocationAndAngles(double scaledX, double scaledY, double scaledZ, double scaledYaw, double scaledPitch) {

    }

    /** get parts for multi-part entities */
    public Entity[] getParts() {
        return null;
    }

    public void func_75687_a(List metadata) {

    }

    public void setVelocity(double scaledSpeedX, double scaledSpeedY, double scaledSpeedZ) {

    }

    public int getEntityId() {
        return 0;
    }

    public DataWatcher getDataWatcher() {
        return null;
    }

    public void setEntityId(int id) {

    }
}
