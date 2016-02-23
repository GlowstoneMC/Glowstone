package net.minecraft.util;

public interface IThreadListener {

    boolean isCallingFromMinecraftThread();

    void addScheduledTask(Runnable runnable);
}
