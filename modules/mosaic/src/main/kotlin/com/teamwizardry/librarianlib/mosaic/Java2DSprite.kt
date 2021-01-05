package com.teamwizardry.librarianlib.mosaic

import com.teamwizardry.librarianlib.core.util.AWTTextureUtil
import com.teamwizardry.librarianlib.core.util.DefaultRenderStates
import com.teamwizardry.librarianlib.core.util.GlResourceGc
import com.teamwizardry.librarianlib.core.util.loc
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.TextureUtil
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

public class Java2DSprite(override val width: Int, override val height: Int) : ISprite {
    private val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    private var natives: Pair<Int, NativeImage> by GlResourceGc.track(this,
        TextureUtil.generateId() to NativeImage(NativeImage.Format.ABGR, width, height, true)
    ) { (texID, native) ->
        if(texID == -1) return@track
        logger.debug("Deleting Java2DSprite $texID")
        TextureUtil.deleteId(texID)
        native.close()
    }
    private val native: NativeImage
        get() = natives.second
    private var texID: Int
        get() = natives.first
        set(value) {
            natives = value to natives.second
        }
    private val deleted: Boolean
        get() = texID == -1

    override val renderType: RenderLayer

    init {
        TextureUtil.allocate(texID, width, height)
        native.upload(0, 0, 0, false)

        val renderState = RenderLayer.MultiPhaseParameters.builder()
            .texture(RenderPhase.Texture(loc("minecraft:missingno"), false, false))
            .alpha(DefaultRenderStates.DEFAULT_ALPHA)
            .depthTest(DefaultRenderStates.DEPTH_LEQUAL)
            .transparency(DefaultRenderStates.TRANSLUCENT_TRANSPARENCY)
//        if(deleted) throw IllegalStateException("Texture has been deleted")

        @Suppress("INACCESSIBLE_TYPE")
        renderType = RenderLayer.of("sprite_type",
            VertexFormats.POSITION_COLOR_TEXTURE, GL11.GL_QUADS, 256, false, false, renderState.build(true)
        )
    }

    @JvmOverloads
    public fun begin(clear: Boolean = true, antialiasing: Boolean = false): Graphics2D {
        val g2d = image.graphics as Graphics2D
        if(antialiasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        }
        g2d.background = Color(0, 0, 0, 0)
        if(clear) g2d.clearRect(0, 0, width, height)
        g2d.color = Color.WHITE
        return g2d
    }

    public fun end() {
        if(deleted) return
        AWTTextureUtil.fillNativeImage(image, native)
        native.upload(0, 0, 0, false)
    }

    public fun delete() {
        if(deleted) return
        TextureUtil.deleteId(texID)
        native.close()
        texID = -1
    }

    override fun minU(animFrames: Int): Float = 0f
    override fun minV(animFrames: Int): Float = 0f
    override fun maxU(animFrames: Int): Float = 1f
    override fun maxV(animFrames: Int): Float = 1f
    override val uSize: Float = 1f
    override val vSize: Float = 1f
    override val frameCount: Int = 1
}
