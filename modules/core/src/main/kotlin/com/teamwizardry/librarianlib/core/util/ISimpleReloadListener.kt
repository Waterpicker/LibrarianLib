package com.teamwizardry.librarianlib.core.util

import net.minecraft.resource.ResourceManager
import net.minecraft.util.profiler.Profiler

public interface ISimpleReloadListener<T> {
    /**
     * Prepare for reloading on a background thread.
     * @return The value to pass to the [apply] method
     */
    public fun prepare(resourceManager: ResourceManager, profiler: Profiler): T

    /**
     * Apply the reload on the main thread.
     * @param result The value returned from [prepare]
     */
    public fun apply(result: T, resourceManager: ResourceManager, profiler: Profiler)
}
