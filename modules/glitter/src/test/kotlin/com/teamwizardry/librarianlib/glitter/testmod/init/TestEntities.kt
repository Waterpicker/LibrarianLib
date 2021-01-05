package com.teamwizardry.librarianlib.glitter.testmod.init

import com.teamwizardry.librarianlib.glitter.testmod.entity.ParticleSpawnerEntity
import com.teamwizardry.librarianlib.glitter.testmod.modid
import net.minecraft.entity.EntityClassification
import net.minecraft.entity.AreaEffectCloudEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries

object TestEntities {
    val spawner = EntityType.Builder.create<ParticleSpawnerEntity>({ _, world ->
        ParticleSpawnerEntity(world)
    }, SpawnGroup.MISC)
        .setCustomClientFactory { _, world ->
            ParticleSpawnerEntity(world)
        }
        .size(0.5f, 0.5f).build("particle_spawner")
        .setRegistryName(modid, "particle_spawner")

    fun register() {
        ForgeRegistries.ENTITIES.register(spawner)
    }
}