package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.core.rendering.BlendMode
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import com.teamwizardry.librarianlib.glitter.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

object SpawnCountAdjustmentSystem: TestSystem("spawn_count_adjustment") {
    override fun configure() {
        val position = bind(3)
        val previousPosition = bind(3)
        val velocity = bind(3)
        val color = bind(4)

        updateModules.add(BasicPhysicsUpdateModule(
            position = position,
            previousPosition = previousPosition,
            velocity = velocity,
            enableCollision = true,
            gravity = ConstantBinding(0.02),
            bounciness = ConstantBinding(0.8),
            friction = ConstantBinding(0.02),
            damping = ConstantBinding(0.01)
        ))

        renderModules.add(SpriteRenderModule(
            renderType = SpriteRenderModule.simpleRenderType(
                sprite = Identifier("librarianlib-glitter-test:textures/glitter/glow.png"),
                blendMode = BlendMode.ADDITIVE,
                writeDepth = false,
                blur = true
            ),
            previousPosition = previousPosition,
            position = position,
            color = color,
            size = ConstantBinding(2.0)
        ))

        ignoreParticleSetting = true
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getCameraPosVec(0f)

        repeat(adjustParticleCount(20)) {
            doSpawn(
                eyePos,
                player.pitch + (Math.random() - 0.5).toFloat() * 40,
                player.yaw + (Math.random() - 0.5).toFloat() * 180
            )
        }
    }

    fun doSpawn(pos: Vec3d, pitch: Float, yaw: Float) {
        val look = getVectorForRotation(pitch, yaw)

        val spawnDistance = 2
        val spawnVelocity = 1.0

        this.addParticle(200,
            // position
            pos.x + look.x * spawnDistance,
            pos.y + look.y * spawnDistance,
            pos.z + look.z * spawnDistance,
            // previous position
            pos.x + look.x * spawnDistance,
            pos.y + look.y * spawnDistance,
            pos.z + look.z * spawnDistance,
            // velocity
            look.x * spawnVelocity,
            look.y * spawnVelocity,
            look.z * spawnVelocity,
            // color
            Math.random() * 0.1,
            Math.random() * 0.1,
            Math.random() * 0.1,
            1.0
        )
    }

    fun getVectorForRotation(pitch: Float, yaw: Float): Vec3d {
        val f = pitch * (Math.PI.toFloat() / 180f)
        val f1 = -yaw * (Math.PI.toFloat() / 180f)
        val f2 = MathHelper.cos(f1)
        val f3 = MathHelper.sin(f1)
        val f4 = MathHelper.cos(f)
        val f5 = MathHelper.sin(f)
        return Vec3d((f3 * f4).toDouble(), (-f5).toDouble(), (f2 * f4).toDouble())
    }
}