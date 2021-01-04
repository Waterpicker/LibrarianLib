package com.teamwizardry.librarianlib.core.util;

import com.google.common.collect.Lists;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.SelectiveReloadStateHandler;

import com.teamwizardry.librarianlib.core.util.sided.ClientRunnable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ResourceReload {
    @Environment(EnvType.CLIENT)
    public boolean isLoading(IResourceType type) {
        return SelectiveReloadStateHandler.INSTANCE.get().test(type);
    }

    public void register(ResourceReloadListener listener) {
        ((ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager()).registerListener(listener);
    }

    public <T> void register(ISimpleReloadListener<T> listener) {
        this.register(new SimpleReloadListener<T>(listener));
    }

    public void register(List<ResourceType> types, ClientRunnable runnable) {
        ISelectiveResourceReloadListener listener = (resourceManager, resourcePredicate) -> {
            boolean hasMatch = false;
            for (IResourceType type : types) {
                hasMatch = hasMatch || resourcePredicate.test(type);
            }
            if (hasMatch)
                runnable.run();
        };

        ((ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager()).registerListener(listener);
    }

    public void register(ResourceType type, ClientRunnable runnable) {
        register(Lists.newArrayList(type), runnable);
    }

    private static class SimpleReloadListener<T> extends SinglePreparationResourceReloadListener<T> {
        private ISimpleReloadListener<T> listener;

        public SimpleReloadListener(ISimpleReloadListener<T> listener) {
            this.listener = listener;
        }

        @Override
        protected T prepare(ResourceManager resourceManagerIn, Profiler profilerIn) {
            return listener.prepare(resourceManagerIn, profilerIn);
        }

        @Override
        protected void apply(T splashList, ResourceManager resourceManagerIn, Profiler profilerIn) {
            listener.apply(splashList, resourceManagerIn, profilerIn);
        }
    }
}
