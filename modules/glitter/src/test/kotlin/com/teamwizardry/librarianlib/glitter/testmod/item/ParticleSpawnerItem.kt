package com.teamwizardry.librarianlib.glitter.testmod.item

import com.teamwizardry.librarianlib.core.util.sided.SidedRunnable
import com.teamwizardry.librarianlib.glitter.testmod.entity.ParticleSpawnerEntity
import com.teamwizardry.librarianlib.glitter.testmod.init.group
import com.teamwizardry.librarianlib.glitter.testmod.modid
import com.teamwizardry.librarianlib.glitter.testmod.systems.ParticleSystems
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.world.World

class ParticleSpawnerItem(val type: String): Item(
    Settings()
        .group(group)
        .maxCount(1)
) {

    init {
        this.registryName = Identifier(modid, "spawn_$type")
    }

    override fun getUseAction(stack: ItemStack): UseAction {
        return UseAction.BOW
    }

    override fun onUsingTick(stack: ItemStack, player: LivingEntity, count: Int) {
        if(player.world.isClient)
            SidedRunnable.client {
                ParticleSystems.spawn(type, player)
            }
    }

    override fun use(worldIn: World, playerIn: PlayerEntity, handIn: Hand): TypedActionResult<ItemStack> {
        if(playerIn.isSneaking) {
            if(!worldIn.isClient) {
                val eye = playerIn.getCameraPosVec(0f)
                val spawner = ParticleSpawnerEntity(worldIn)
                spawner.system = type
                spawner.setPos(eye.x, eye.y - spawner.eyeY, eye.z)
                spawner.pitch = playerIn.pitch
                spawner.yaw = playerIn.yaw
                worldIn.spawnEntity(spawner)
            }
        } else {
            playerIn.setCurrentHand(handIn)
        }
        return TypedActionResult(ActionResult.SUCCESS, playerIn.getStackInHand(handIn))
    }

    override fun getMaxUseTime(stack: ItemStack?): Int {
        return 3600 * 20 // an hour :P
    }
}
