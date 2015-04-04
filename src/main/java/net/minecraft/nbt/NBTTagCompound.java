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
package net.minecraft.nbt;

import java.util.Set;

public class NBTTagCompound extends NBTBase {

    public void func_74782_a(String key, NBTTagList list) {

    }

    /** get tag list */
    public NBTTagList getTagList(String key) {
        return null;
    }

    public NBTTagList getTagList(String key, int b) {
        return null;
    }

    /** get string */
    public String getString(String key) {
        return null;
    }

    /** get int */
    public int getInteger(String key) {
        return 0;
    }

    /** get compound tag */
    public NBTTagCompound getCompoundTag(String key) {
        return null;
    }

    public Set<String> getKeySet() {
        return null;
    }

    /** get whether a key exists */
    public boolean hasKey(String key) {
        return true;
    }

    public boolean hasKey(String key, int i) {
        return true;
    }


    /** set string */
    public void setString(String key, String value) {

    }

    /** set int */
    public void setInteger(String key, int value) {

    }

    /** set compound tag */
    public void setTag(String key, NBTTagCompound tagCompound) {

    }

    public void setTag(String key, NBTTagList tagList) {

    }

    public void setIntArray(String key, int[] array) {

    }

    public int[] getIntArray(String key) {
        return null;
    }
}
