package net.glowstone.block;

import com.destroystokyo.paper.block.BlockSoundGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.glowstone.util.SoundInfo;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class GlowBlockSoundGroup implements BlockSoundGroup {

    @Getter
    private SoundInfo breakSoundInfo;
    @Getter
    private SoundInfo stepSoundInfo;
    @Getter
    private SoundInfo placeSoundInfo;
    @Getter
    private SoundInfo hitSoundInfo;
    @Getter
    private SoundInfo fallSoundInfo;


    @Override
    public @NotNull Sound getBreakSound() {
        return breakSoundInfo.getSound();
    }

    @Override
    public @NotNull Sound getStepSound() {
        return stepSoundInfo.getSound();
    }

    @Override
    public @NotNull Sound getPlaceSound() {
        return placeSoundInfo.getSound();
    }

    @Override
    public @NotNull Sound getHitSound() {
        return hitSoundInfo.getSound();
    }

    @Override
    public @NotNull Sound getFallSound() {
        return fallSoundInfo.getSound();
    }
}
