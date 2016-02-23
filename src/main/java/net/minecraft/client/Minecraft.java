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

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraftforge.fml.client.FMLClientHandler;
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
import java.util.ArrayList;
import java.util.List;

public class Minecraft implements IThreadListener {


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

    public static Minecraft getMinecraft() {
        return null; // TODO: singleton instance
    }

    public FontRenderer fontRendererObj;

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

    public static void fmlReentry(/*ArgsWrapper wrapper*/) {
        System.out.println("entered fmlReentry");

        System.out.println("MLIA loading...");
        FMLClientHandler.instance().beginMinecraftLoading(new Minecraft(), new ArrayList(), null);
        System.out.println("Finishing loading");
        FMLClientHandler.instance().finishMinecraftLoading();
        System.out.println("Initialization completing");
        FMLClientHandler.instance().onInitializationComplete();
        System.out.println("Done");
    }

    public boolean isDemo() {
        return false;
    }

    public void refreshResources() {

    }

    public void displayCrashReport(CrashReport crashReport) {
        System.out.println("We crashed: "+crashReport);
    }

    public void displayGuiScreen(GuiScreen gui) {

    }

    public GuiScreen currentScreen;
    public GuiScreen loadingScreen;
    public int displayWidth;
    public int displayHeight;
    public WorldClient theWorld;
    public EntityPlayerSP thePlayer;

    public SaveFormatOld getSaveLoader() {
        return null;
    }

    public MinecraftServer getIntegratedServer() {
        return null;
    }

    public TextureManager getTextureManager() {
        return null;
    }

    public GameSettings gameSettings;

    public File mcDataDir;

    public void launchIntegratedServer(String dirName, String saveName, WorldSettings worldSettings) {

    }

    public Object getSoundHandler() { // TODO: what class is this?
        return null;
    }

    @Override
    public boolean isCallingFromMinecraftThread() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addScheduledTask(Runnable runnable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public RenderEngine renderEngine;

    public RenderEngine getRenderManager() {
        return renderEngine;
    }

    public LanguageManager getLanguageManager() {
        return null;
    }

    public class LanguageManager {
        public Language getCurrentLanguage() {
            return null;
        }

        public class Language {
            public String getLanguageCode() {
                return "en";
            }
        }
    }

    public NetHandlerPlayClient getNetHandler() {
        return null;
    }
}
