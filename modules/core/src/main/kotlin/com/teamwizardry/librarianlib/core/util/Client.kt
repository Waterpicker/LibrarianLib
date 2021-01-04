package com.teamwizardry.librarianlib.core.util

import com.teamwizardry.librarianlib.math.Vec2d
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.render.Tessellator
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.texture.TextureManager
import net.minecraft.client.util.Window
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CompletableFuture

public object Client {
    @JvmStatic
    public val minecraft: MinecraftClient
        get() = MinecraftClient.getInstance()

    @JvmStatic
    public val player: ClientPlayerEntity?
        get() = minecraft.player

    @JvmStatic
    public val window: Window
        get() = minecraft.window

    @JvmStatic
    public val guiScaleFactor: Double
        get() = window.scaleFactor

    @JvmStatic
    public val resourceManager: ResourceManager
        get() = minecraft.resourceManager

    @JvmStatic
    public val textureManager: TextureManager
        get() = minecraft.textureManager

    @JvmStatic
    public val fontRenderer: TextRenderer
        get() = minecraft.textRenderer

    @JvmStatic
    public val tessellator: Tessellator
        get() = Tessellator.getInstance()

    /**
     * Datagen is like a quasi-client, so some things don't work. For example, there's no [Minecraft] instance.
     */
    @JvmStatic
    public val isDataGen: Boolean =
        Thread.getAllStackTraces().any { trace ->
            trace.value.any { element ->
                element.className == "net.minecraft.data.Main"
            }
        }

    /**
     * The game time, as measured from the game launch
     */
    @JvmStatic
    public val time: Time = object: Time() {
        override val ticks: Int
            get() = globalTicks
        override val partialTicks: Float
            get() = timer.tickDelta
    }

    /**
     * The world time, as measured from the game launch
     */
    @JvmStatic
    public val worldTime: Time = object: Time() {
        override val ticks: Int
            get() = worldTicks
        override val partialTicks: Float
            get() = if (minecraft.isPaused)
                renderPartialTicksPaused.get(minecraft) as Float
            else
                timer.tickDelta
    }

    @JvmStatic
    public val resourceReloadHandler: ResourceReload = ResourceReload()

    @JvmStatic
    public fun displayGuiScreen(screen: Screen?) {
        minecraft.openScreen(screen)
    }

    /**
     * Queue a task to be executed on the client thread. The task is executed immediately if this is called from the
     * client thread.
     */
    @JvmStatic
    public fun runAsync(task: Runnable): CompletableFuture<Void> {
        return minecraft.submit(task)
    }

    @JvmStatic
    public fun getBlockAtlasSprite(sprite: Identifier): Sprite {
        @Suppress("DEPRECATION")
        return getAtlasSprite(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, sprite)
    }

    @JvmStatic
    public fun getAtlasSprite(atlas: Identifier, texture: Identifier): Sprite {
        return minecraft.getSpriteAtlas(atlas).apply(texture)
    }

    /**
     * Gets the [InputStream] for a given resource, or throws an IOException if it isn't found
     *
     * @see getResourceInputStreamOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceInputStream(resourceManager: ResourceManager, location: Identifier): InputStream {
        return resourceManager.getResource(location).inputStream
    }

    /**
     * Gets the [InputStream] for a given resource, or returns null if it isn't found
     *
     * @see getResourceInputStream
     */
    @JvmStatic
    public fun getResourceInputStreamOrNull(resourceManager: ResourceManager, location: Identifier): InputStream? {
        return try {
            getResourceInputStream(resourceManager, location)
        } catch (e: IOException) {
            null
        }
    }

    /**
     * Gets the contents of the given resource as a byte array, or throws an IOException if the resource isn't found
     *
     * @see getResourceBytesOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceBytes(resourceManager: ResourceManager, location: Identifier): ByteArray {
        return resourceManager.getResource(location).inputStream.use { it.readBytes() }
    }

    /**
     * Gets the contents of the given resource as a byte array, or returns null if the resource isn't found
     *
     * @see getResourceBytes
     */
    @JvmStatic
    public fun getResourceBytesOrNull(resourceManager: ResourceManager, location: Identifier): ByteArray? {
        return try {
            getResourceBytes(resourceManager, location)
        } catch (e: IOException) {
            null
        }
    }

    /**
     * Gets the contents of the given resource as a string, or throws an IOException if the resource isn't found
     *
     * @see getResourceTextOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceText(resourceManager: ResourceManager, location: Identifier): String {
        return resourceManager.getResource(location).inputStream.bufferedReader().use { it.readText() }
    }

    /**
     * Gets the contents of the given resource as a string, or returns null if the resource isn't found
     *
     * @see getResourceText
     */
    @JvmStatic
    public fun getResourceTextOrNull(resourceManager: ResourceManager, location: Identifier): String? {
        return try {
            getResourceText(resourceManager, location)
        } catch (e: IOException) {
            null
        }
    }

    /**
     * Gets the [InputStream] for a given resource, or throws an IOException if it isn't found
     *
     * @see getResourceInputStreamOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceInputStream(location: Identifier): InputStream {
        return getResourceInputStream(resourceManager, location)
    }

    /**
     * Gets the [InputStream] for a given resource, or returns null if it isn't found
     *
     * @see getResourceInputStream
     */
    @JvmStatic
    public fun getResourceInputStreamOrNull(location: Identifier): InputStream? {
        return getResourceInputStreamOrNull(resourceManager, location)
    }

    /**
     * Gets the contents of the given resource as a byte array, or throws an IOException if the resource isn't found
     *
     * @see getResourceBytesOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceBytes(location: Identifier): ByteArray {
        return getResourceBytes(resourceManager, location)
    }

    /**
     * Gets the contents of the given resource as a byte array, or returns null if the resource isn't found
     *
     * @see getResourceBytes
     */
    @JvmStatic
    public fun getResourceBytesOrNull(location: Identifier): ByteArray? {
        return getResourceBytesOrNull(resourceManager, location)
    }

    /**
     * Gets the contents of the given resource as a string, or throws an IOException if the resource isn't found
     *
     * @see getResourceTextOrNull
     */
    @JvmStatic
    @Throws(IOException::class)
    public fun getResourceText(location: Identifier): String {
        return getResourceText(resourceManager, location)
    }

    /**
     * Gets the contents of the given resource as a string, or returns null if the resource isn't found
     *
     * @see getResourceText
     */
    @JvmStatic
    public fun getResourceTextOrNull(location: Identifier): String? {
        return getResourceTextOrNull(resourceManager, location)
    }

    public abstract class Time {
        public abstract val ticks: Int
        public abstract val partialTicks: Float
        public val time: Float get() = ticks + partialTicks
        public val seconds: Float get() = time / 20

        public fun interp(previous: Double, current: Double): Double {
            return previous + (current - previous) * partialTicks
        }

        @Suppress("NOTHING_TO_INLINE")
        public inline fun interp(previous: Number, current: Number): Double = interp(previous.toDouble(), current.toDouble())

        public fun interp(previous: Vec2d, current: Vec2d): Vec2d {
            return vec(interp(previous.x, current.x), interp(previous.y, current.y))
        }

        public fun interp(previous: Vec3d, current: Vec3d): Vec3d {
            return vec(interp(previous.x, current.x), interp(previous.y, current.y), interp(previous.z, current.z))
        }
    }

    private val timer: RenderTickCounter = if(isDataGen)
        RenderTickCounter(50f, 0)
    else
        Mirror.reflectClass<MinecraftClient>().getField(mapSrgName("field_71428_T")).get(minecraft)

    private val renderPartialTicksPaused = Mirror.reflectClass<MinecraftClient>().getField(mapSrgName("field_193996_ah"))

    private var worldTicks: Int = 0
    private var globalTicks: Int = 0

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @JvmSynthetic
    @SubscribeEvent
    internal fun clientTickEnd(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            val mc = MinecraftClient.getInstance()
            if (!mc.isPaused)
                worldTicks += timer.elapsedTicks
            globalTicks += timer.elapsedTicks
        }
    }
}
