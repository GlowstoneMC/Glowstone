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
package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public class GuiButton {

    public boolean visible;
    public boolean hovered;
    public boolean enabled;
    public int xPosition;
    public int yPosition;
    public int zLevel;
    public int width;
    public int height;
    public ResourceLocation buttonTextures;
    public int packedFGColour;
    public String displayString;

    public GuiButton(int id, int xPos, int yPos, int width, int height, String displayString) {

    }

    public GuiButton(int id, int xPos, int yPos, String displayString) {

    }

    public int getHoverState(boolean b) {
        return 0;
    }

    public void mouseDragged(Minecraft client, int x, int y) {

    }

    public boolean mousePressed(Minecraft client, int x, int y) {
        return false;
    }

    public boolean mouseReleased(int x, int y) {
        return false;
    }

    public void playPressSound(/*soundhandler*/) {

    }
}
