package com.teamwizardry.librarianlib.features.sprite

import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
object DrawingUtil {
    var isDrawing = false

    /**
     * Start drawing multiple quads to be pushed to the GPU at once
     */
    fun startDrawingSession() {
        Tessellator.getInstance().buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        isDrawing = true
    }

    /**
     * Finish drawing multiple quads and push them to the GPU
     */
    fun endDrawingSession() {
        Tessellator.getInstance().draw()
        isDrawing = false
    }

    /**
     * **!!! Use [Sprite.draw] or [Sprite.draw] instead !!!**

     *
     * Draw a sprite at a location with the width and height specified.
     * @param sprite The sprite to draw
     * *
     * @param x The x position to draw at
     * *
     * @param y The y position to draw at
     * *
     * @param width The width to draw the sprite
     * *
     * @param height The height to draw the sprite
     */
    fun draw(sprite: ISprite, animFrames: Int, x: Float, y: Float, width: Float, height: Float) {

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        if (!isDrawing)
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        if(!sprite.hardScaleU && !sprite.hardScaleV &&
            sprite.minUCap == 0f && sprite.minVCap == 0f && sprite.maxUCap == 0f && sprite.maxVCap == 0f) {
            drawSimple(sprite, animFrames, x, y, width, height)
        } else {
            drawComplex(sprite, animFrames, x, y, width, height)
        }

        if (!isDrawing)
            tessellator.draw()
    }

    private fun drawSimple(sprite: ISprite, animFrames: Int, x: Float, y: Float, width: Float, height: Float) {
        val minX = x
        val minY = y
        val maxX = x + width
        val maxY = y + height

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.pos(minX.toDouble(), maxY.toDouble(), 0.0).tex(sprite.minU(animFrames).toDouble(), sprite.maxV(animFrames).toDouble()).endVertex()
        vb.pos(maxX.toDouble(), maxY.toDouble(), 0.0).tex(sprite.maxU(animFrames).toDouble(), sprite.maxV(animFrames).toDouble()).endVertex()
        vb.pos(maxX.toDouble(), minY.toDouble(), 0.0).tex(sprite.maxU(animFrames).toDouble(), sprite.minV(animFrames).toDouble()).endVertex()
        vb.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(sprite.minU(animFrames).toDouble(), sprite.minV(animFrames).toDouble()).endVertex()
    }

    private fun drawComplex(sprite: ISprite, animFrames: Int, x: Float, y: Float, width: Float, height: Float) {

        val xSections = getSections(
            size = sprite.width.toFloat(),
            minCap = sprite.minUCap,
            maxCap = sprite.maxUCap,
            hard = sprite.hardScaleU,
            targetPos = width
        )

        val ySections = getSections(
            size = sprite.height.toFloat(),
            minCap = sprite.minVCap,
            maxCap = sprite.maxVCap,
            hard = sprite.hardScaleV,
            targetPos = height
        )

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        val spriteMinU = sprite.minU(animFrames)
        val spriteMinV = sprite.minV(animFrames)
        val spriteUSpan = sprite.maxU(animFrames) - spriteMinU
        val spriteVSpan = sprite.maxV(animFrames) - spriteMinV
        xSections.forEach { xSection ->
            ySections.forEach { ySection ->
                val minX = x + xSection.minPos
                val minY = y + ySection.minPos
                val maxX = x + xSection.maxPos
                val maxY = y + ySection.maxPos
                val minU = spriteMinU + xSection.minTex * spriteUSpan
                val minV = spriteMinV + ySection.minTex * spriteVSpan
                val maxU = spriteMinU + xSection.maxTex * spriteUSpan
                val maxV = spriteMinV + ySection.maxTex * spriteVSpan
                vb.pos(minX.toDouble(), maxY.toDouble(), 0.0).tex(minU.toDouble(), maxV.toDouble()).endVertex()
                vb.pos(maxX.toDouble(), maxY.toDouble(), 0.0).tex(maxU.toDouble(), maxV.toDouble()).endVertex()
                vb.pos(maxX.toDouble(), minY.toDouble(), 0.0).tex(maxU.toDouble(), minV.toDouble()).endVertex()
                vb.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(minU.toDouble(), minV.toDouble()).endVertex()
            }
        }
    }

    private fun getSections(size: Float, minCap: Float, maxCap: Float, hard: Boolean, targetPos: Float): List<Section> {
        val sections = mutableListOf<Section>()
        if(!hard) {
            if(minCap != 0f) {
                sections.add(Section(0f, size * minCap, 0f, minCap))
            }
            sections.add(Section(size * minCap, targetPos - size * maxCap, minCap, 1-maxCap))
            if(maxCap != 0f) {
                sections.add(Section(targetPos - size * maxCap, targetPos, 1-maxCap, 1f))
            }
        } else {
            val midSize = size * (1 - minCap - maxCap)

            var pos = 0f
            if(minCap != 0f) {
                sections.add(Section(pos, size * minCap, 0f, minCap))
                pos += size * minCap
            }

            // generate a bunch of middle sections
            val endCapStart = targetPos - size * maxCap
            do {
                sections.add(Section(pos, pos + midSize, minCap, 1-maxCap))
                pos += midSize
            } while (pos < endCapStart)

            // trim last section to required size
            val cut = pos - endCapStart
            sections.last().maxPos = cut
            sections.last().maxTex = minCap + cut / size
            pos = endCapStart

            if(maxCap != 0f) {
                sections.add(Section(pos, targetPos, 1-maxCap, 1f))
            }
        }
        return sections
    }

    private class Section(var minPos: Float, var maxPos: Float, var minTex: Float, var maxTex: Float)
}
