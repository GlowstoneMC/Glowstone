package net.minecraft.launchwrapper;

public interface IClassTransformer {

    byte[] transform(String name, String transformedName, byte[] bytes);
}
