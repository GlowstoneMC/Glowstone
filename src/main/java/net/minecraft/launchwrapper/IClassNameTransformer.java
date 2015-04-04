package net.minecraft.launchwrapper;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public interface IClassNameTransformer {

    String remapClassName(String name);

    String unmapClassName(String name);
}
