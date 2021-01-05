package com.teamwizardry.librarianlib.etcetera.testmod

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType

object Particles {
    val TARGET_RED: DefaultParticleType = DefaultParticleType(true).also {
        it.setRegistryName("librarianlib-etcetera-test:target_red")
    }
    val TARGET_BLUE: DefaultParticleType = DefaultParticleType(true).also {
        it.setRegistryName("librarianlib-etcetera-test:target_blue")
    }
}

@Environment(EnvType.CLIENT)
class HitParticle private constructor(world: ClientWorld, x: Double, y: Double, z: Double): SpriteBillboardParticle(world, x, y, z, 0.0, 0.0, 0.0) {
    init {
        maxAge = 1
        collidesWithWorld = false
    }

    override fun getType(): ParticleTextureSheet {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE
    }

    override fun getSize(p_217561_1_: Float): Float {
        return 0.5f
    }

    override fun tick() {
        prevPosX = x
        prevPosY = y
        prevPosZ = z
        if (age++ >= maxAge) {
            markDead()
        }
    }

    @Environment(EnvType.CLIENT)
    class Factory(private val spriteSet: SpriteProvider): ParticleFactory<DefaultParticleType> {
        override fun createParticle(typeIn: DefaultParticleType, worldIn: ClientWorld, x: Double, y: Double, z: Double, xSpeed: Double, ySpeed: Double, zSpeed: Double): Particle? {
            val hitParticle = HitParticle(worldIn, x, y, z)
            hitParticle.setSprite(spriteSet)
            return hitParticle
        }
    }

}