package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ParticleBinding

class StoredBinding(private val index: Int, private val size: Int): ParticleBinding {
    override fun getSize(): Int = size

    override fun get(particle: DoubleArray, index: Int): Double {
        return particle[this.index + index]
    }

    override fun set(particle: DoubleArray, index: Int, value: Double) {
        particle[this.index + index] = value
    }

    fun set(particle: DoubleArray, vararg values: Double) {
        (0 until index).forEach {
            particle[this.index + it] = values[it]
        }
    }
}