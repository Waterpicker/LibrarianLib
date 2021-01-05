package com.teamwizardry.librarianlib.albedo.testmod.shaders

import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.testmod.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import java.awt.Color

internal object IntUniform: ShaderTest<IntUniform.Test>() {

    override fun doDraw() {
        val minX = 0.0
        val minY = 0.0
        val maxX = 128.0
        val maxY = 128.0

        val c = Color.WHITE

        shader.primitive.set(10)
        shader.vector2.set(10, 20)
        shader.vector3.set(10, 20, 30)
        shader.vector4.set(10, 20, 30, 40)

        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        vb.pos2d(minX, maxY).color(c).texture(0f, 1f).next()
        vb.pos2d(maxX, maxY).color(c).texture(1f, 1f).next()
        vb.pos2d(maxX, minY).color(c).texture(1f, 0f).next()
        vb.pos2d(minX, minY).color(c).texture(0f, 0f).next()

        shader.bind()
        buffer.draw()
        shader.unbind()
    }

    private val renderType = SimpleRenderTypes.flat(Identifier("minecraft:missingno"), GL11.GL_QUADS)

    class Test: Shader("int_tests", null, Identifier("librarianlib-albedo-test:shaders/int_tests.frag")) {
        val primitive = GLSL.glInt()
        val vector2 = GLSL.ivec2()
        val vector3 = GLSL.ivec3()
        val vector4 = GLSL.ivec4()
    }
}