package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.*

/**
 * Performs rudimentary velocity updates.
 *
 * The process of this module is simple but useful, first it copies [position] into [previousPosition],
 * then adds [velocity] to [position] and stores it back in [position]
 */
class VelocityUpdateModule(
        /**
         * The position to be moved based upon [velocity]
         */
        @JvmField val position: ReadWriteParticleBinding,
        /**
         * The velocity to moved [position] by
         */
        @JvmField val velocity: ReadParticleBinding,
        /**
         * The binding to store the position in before modifying it, if desired
         */
        @JvmField val previousPosition: WriteParticleBinding? = null
): ParticleUpdateModule {
    init {
        position.require(3)
        velocity.require(3)
        previousPosition?.require(3)
    }

    override fun update(particle: DoubleArray) {
        update(particle, 0)
        update(particle, 1)
        update(particle, 2)
    }

    private fun update(particle: DoubleArray, index: Int) {
        val pos = position[particle, index]
        previousPosition?.set(particle, index, pos)
        position[particle, index] = pos + velocity[particle, index]
    }
}