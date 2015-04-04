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
package net.minecraft.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.ArgsWrapper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.io.File;
import java.io.InputStream;

public class Minecraft {


    public RenderEngine field_71446_o = new RenderEngine();

    /* texture packs - not implemented */
    public Minecraft field_71418_C;
    /** get current texture path */
    public Minecraft func_77292_e() { return this; }
    /** open stream for image path in current texture path */
    public InputStream func_77532_a(String path) { return null; }

    /** current world loaded, or null if none loaded */
    public WorldClient field_71441_e;

    public GuiScreen field_71462_r;

    /** game settings, including key bindings */
    public GameSettings field_71474_y = new GameSettings();

    public EntityPlayer field_71439_g;

    /** home directory (minecraftDir, an) */
    public static File field_71463_am = new File(".");

    public void func_71373_a(Object o) {

    }

    /** are in we in demo mode? */
    public boolean func_71355_q() {
        return false;
    }

    public void func_71377_b(CrashReport crashReport) {
        System.out.println("Crashed :(" + crashReport);
    }

    /** get server */
    public MinecraftServer func_71401_C() {
        return null;
    }

    public void continueWorldLoading() {

    }

    /** reset client state */
    public void func_71403_a(WorldClient worldClient) {

    }

    /** get root directory (called in FMLRelauncher) */
    public static String func_71380_b() { // aka getMinecraftDir() or b()
        return ".";
    }

    public static void fmlReentry(ArgsWrapper wrapper) {
        System.out.println("entered fmlReentry");

        System.out.println("MLIA loading...");
        FMLClientHandler.instance().beginMinecraftLoading(new Minecraft());
        System.out.println("Finishing loading");
        FMLClientHandler.instance().finishMinecraftLoading();
        System.out.println("Initialization completing");
        FMLClientHandler.instance().onInitializationComplete();
        System.out.println("Done");
    }
}
