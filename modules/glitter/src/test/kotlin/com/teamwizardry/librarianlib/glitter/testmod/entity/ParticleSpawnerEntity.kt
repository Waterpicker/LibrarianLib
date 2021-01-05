package com.teamwizardry.librarianlib.glitter.testmod.entity

import com.teamwizardry.librarianlib.core.util.sided.SidedRunnable
import com.teamwizardry.librarianlib.glitter.testmod.init.TestEntities
import com.teamwizardry.librarianlib.glitter.testmod.systems.ParticleSystems
import net.minecraft.entity.Entity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.world.World
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Packet
import net.minecraft.network.datasync.EntityDataManager
import net.minecraftforge.fml.network.NetworkHooks

class ParticleSpawnerEntity(world: World): Entity(TestEntities.spawner, world) {
    var system: String
        get() = this.dataTracker[SYSTEM]
        set(value) {
            this.dataTracker[SYSTEM] = value
        }

    override fun createSpawnPacket(): Packet<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun readCustomDataFromTag(compound: CompoundTag) {
        system = compound.getString("System")
    }

    override fun writeCustomDataToTag(compound: CompoundTag) {
        compound.putString("System", system)
    }

    override fun initDataTracker() {
        dataTracker.set(SYSTEM, "")
    }

    override fun collides(): Boolean {
        return true
    }

    override fun tick() {
        super.tick()
        if(world.isClient) {
            SidedRunnable.client {
                ParticleSystems.spawn(system, this)
            }
        }
    }

    override fun handleAttack(entity: Entity): Boolean {
        if(entity is PlayerEntity) {
            this.remove()
            return true
        }
        return false
    }

    companion object {
        val SYSTEM = DataTracker.registerData(ParticleSpawnerEntity::class.java, TrackedDataHandlerRegistry.STRING)
    }

}