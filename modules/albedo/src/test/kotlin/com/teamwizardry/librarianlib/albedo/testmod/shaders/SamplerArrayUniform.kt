package com.teamwizardry.librarianlib.albedo.testmod.shaders

import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.testmod.ShaderTest
import com.teamwizardry.librarianlib.core.bridge.IMutableRenderTypeState
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DefaultRenderStates
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.mixinCast
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import java.awt.Color

internal object SamplerArrayUniform: ShaderTest<SamplerArrayUniform.Test>() {
    private val failureLocation = Identifier("librarianlib-albedo-test:textures/sampler_failure.png")
    private val successLocation1 = Identifier("librarianlib-albedo-test:textures/sampler_success1.png")
    private val successLocation2 = Identifier("librarianlib-albedo-test:textures/sampler_success2.png")

    override fun doDraw() {
        val minX = 0.0
        val minY = 0.0
        val maxX = 128.0
        val maxY = 128.0

        val c = Color.WHITE

        Client.textureManager.bindTexture(successLocation1)
        Client.textureManager.bindTexture(successLocation2)
        val tex1 = Client.textureManager.getTexture(successLocation1)?.glId ?: throw IllegalStateException("sampler_success1 not found")
        val tex2 = Client.textureManager.getTexture(successLocation2)?.glId ?: throw IllegalStateException("sampler_success2 not found")
        Client.textureManager.bindTexture(failureLocation)

        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        var vb = buffer.getBuffer(renderType)
        vb.pos2d(minX, maxY).color(c).texture(0f, 1f).next()
        vb.pos2d(maxX, maxY).color(c).texture(1f, 1f).next()
        vb.pos2d(maxX, minY).color(c).texture(1f, 0f).next()
        vb.pos2d(minX, minY).color(c).texture(0f, 0f).next()

        buffer.draw()

        val index = (Client.time.seconds % 2).toInt()
        shader.index.set(index)
        shader.sampler1[0] = tex2
        shader.sampler1[1] = tex1
        shader.sampler2[0] = tex1
        shader.sampler2[1] = tex2

        vb = buffer.getBuffer(renderType)

        vb.pos2d(minX, maxY).color(c).texture(0f, 2f).next()
        vb.pos2d(maxX, maxY).color(c).texture(1f, 2f).next()
        vb.pos2d(maxX, minY).color(c).texture(1f, 0f).next()
        vb.pos2d(minX, minY).color(c).texture(0f, 0f).next()

        buffer.draw()
    }

    private val renderType: RenderLayer
    init {

        val renderState = RenderLayer.MultiPhaseParameters.builder()
            .alpha(DefaultRenderStates.DEFAULT_ALPHA)
            .depthTest(DefaultRenderStates.DEPTH_LEQUAL)
            .transparency(DefaultRenderStates.TRANSLUCENT_TRANSPARENCY)
            .build(true)

        mixinCast<IMutableRenderTypeState>(renderState).addPhase("albedo", { shader.bind() }, { shader.unbind() })

        renderType = SimpleRenderTypes.makeType("flat_texture",
            VertexFormats.POSITION_COLOR_TEXTURE, GL11.GL_QUADS, 256, false, false, renderState
        )
    }

    class Test: Shader("sampler_array_tests", null, Identifier("librarianlib-albedo-test:shaders/sampler_array_tests.frag")) {
        val index = GLSL.glInt()
        // we only test sampler2D because all the sampler implementations are identical, and the others will be complex
        // to set up
        val sampler1 = GLSL.sampler2D[2]
        val sampler2 = GLSL.sampler2D[2]
    }
}