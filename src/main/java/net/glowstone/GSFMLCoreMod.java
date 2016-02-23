package net.glowstone;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.8")
public class GSFMLCoreMod implements IFMLLoadingPlugin {

    public GSFMLCoreMod() {
        System.out.println("GSFMLCoreMod loaded");

        // fixes https://github.com/deathcap/Glowstone/issues/2 java.lang.ClassCircularityError with wrapper.net.minecraftforge.fml.common.asm.transformers.EventSubscriberTransformer
        Launch.classLoader.addTransformerExclusion("org.apache.commons.lang3");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
