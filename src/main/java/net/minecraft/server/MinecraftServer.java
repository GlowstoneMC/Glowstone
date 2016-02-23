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
package net.minecraft.server;

import net.minecraft.command.CommandHandler;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.List;

public class MinecraftServer implements IThreadListener {

    public List<String> pendingCommandList;

    public static void main(String[] args) {
        new MinecraftServer().main2(args);
    }

    public void main2(String[] args) {
        System.out.println("about to invoke FML beginServerLoading()");
        FMLServerHandler.instance().beginServerLoading(this);
        System.out.println("Finished loading");
        FMLServerHandler.instance().finishServerLoading();
        System.out.println("Initialization completed");

        List<ModContainer> mods = Loader.instance().getModList();
        for (ModContainer mod : mods) {
            System.out.println("Modification loaded: " + mod.getName() + " " + mod.getVersion());
        }


        //FMLServerHandler.instance().
        //net.minecraftforge.fml.common.FMLCommonHandler.instance().handleServerStarted();
        //net.minecraftforge.fml.common.FMLCommonHandler.instance().handleServerStopping();
        //net.minecraftforge.fml.common.FMLCommonHandler.instance().handleServerStopped();
    }

    public CommandHandler func_71187_D() {
        return null;
    }

    public ServerConfigurationManager func_71203_ab() {
        return null;
    }

    public SaveFormatOld getActiveAnvilConverter() {
        return null;
    }

    public boolean isServerRunning() {
        return true;
    }

    public boolean serverIsInRunLoop() {
        return true;
    }

    public String getFolderName() {
        return ".";
    }

    public CommandHandler getCommandManager() {
        return null;
    }

    public void initiateShutdown() {
        System.out.println("Shutting down");
        System.exit(0);
    }

    public ServerConfigurationManager getConfigurationManager() {
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
}
