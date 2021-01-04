package com.teamwizardry.librarianlib.core.mixin;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

import com.teamwizardry.librarianlib.core.bridge.IMutableRenderTypeState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderLayer.MultiPhaseParameters.class)
public abstract class RenderTypeStateMixin implements IMutableRenderTypeState {
    @Override
    public void addPhase(RenderPhase state) {
        ArrayList<RenderPhase> states = new ArrayList<>(getPhases());
        states.add(state);
        setPhases(ImmutableList.copyOf(states));
    }

    @Accessor
    @Mutable
    @Override
    public abstract ImmutableList<RenderPhase> getPhases();

    @Accessor
    @Mutable
    @Override
    public abstract void setPhases(ImmutableList<RenderPhase> renderStates);
}
