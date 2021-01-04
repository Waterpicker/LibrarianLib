package com.teamwizardry.librarianlib.glitter.bindings;

import net.minecraft.client.shader.Framebuffer;

import net.minecraftforge.fml.network.NetworkEvent;

import com.teamwizardry.librarianlib.glitter.ReadParticleBinding;
import org.jetbrains.annotations.NotNull;

public class CallbackBinding implements ReadParticleBinding {
    private final double[] contents;
    private final Callback callback;

    public CallbackBinding(int size, Callback callback) {
        this.contents = new double[size];
        this.callback = callback;
        Framebuffer f;
        f.enableStencil();
    }

    @NotNull
    @Override
    public double[] getContents() {
        return contents;
    }

    @Override
    public void load(@NotNull double[] particle) {
        callback.call(particle, contents);
    }

    @FunctionalInterface
    public interface Callback {
        void call(@NotNull double[] particle, @NotNull double[] contents);
    }
}
