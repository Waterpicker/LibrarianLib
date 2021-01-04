package com.teamwizardry.librarianlib.core.mixin;

import com.teamwizardry.librarianlib.core.util.GlResourceGc;
import net.minecraft.client.MinecraftClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void runGlResourceGc(boolean renderWorldIn, CallbackInfo ci) {
        GlResourceGc.INSTANCE.releaseCollectedResources$core();
    }
}
