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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiScreen extends Gui {

    public int width;
    public int height;
    protected FontRenderer fontRendererObj;
    public Minecraft mc;
    public List<GuiButton> buttonList = new ArrayList<>();
    public List<Object> labelList = new ArrayList<>();
    public double field_73735_i;
    public int zLevel;

    public GuiScreen() {

    }

    public void initGui() {

    }

    public void func_73866_w_() {

    }

    public void func_73873_v_() {

    }

    /** draw text */
    protected void func_73732_a(FontRenderer fontRenderer, String text, int x, int offset, int color) {
        System.out.println("GuiScreen: " + text);
    }

    /** draw the whole screen */
    public void drawScreen(int a, int b, float c) {

    }

    protected void func_73875_a(GuiButton button) {

    }

    public void handleInput() {

    }

    public void handleMouseInput() throws IOException {

    }


    public void updateScreen() {

    }

    public void drawDefaultBackground() {

    }

    protected void actionPerformed(GuiButton guiButton) throws IOException {

    }


    public void drawHoveringText(List stringList, int x, int y) {

    }

    protected void mouseClicked(int x, int y, int mouseEvent) throws IOException {

    }

    protected void mouseReleased(int x, int y, int mouseEvent) {

    }
}
