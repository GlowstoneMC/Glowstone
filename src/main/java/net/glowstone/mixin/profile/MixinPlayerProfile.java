package net.glowstone.mixin.profile;

import net.glowstone.entity.meta.profile.PlayerProfile;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerProfile.class)
public abstract class MixinPlayerProfile implements GameProfile {
}
