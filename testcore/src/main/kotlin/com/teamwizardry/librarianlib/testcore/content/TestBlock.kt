package com.teamwizardry.librarianlib.testcore.content

import com.teamwizardry.librarianlib.core.util.append
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.TestModResourceManager
import com.teamwizardry.librarianlib.testcore.content.impl.TestBlockImpl
import com.teamwizardry.librarianlib.testcore.content.impl.TestBlockItem
import com.teamwizardry.librarianlib.testcore.objects.TestObjectDslMarker
import com.teamwizardry.librarianlib.testcore.util.PlayerTestContext
import com.teamwizardry.librarianlib.testcore.util.SidedAction
import net.minecraft.block.*
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

@TestObjectDslMarker
public class TestBlock(manager: TestModContentManager, id: Identifier): TestConfig(manager, id) {
    public val properties: AbstractBlock.Settings = AbstractBlock.Settings.of(testMaterial)

    init {
        properties.nonOpaque()
    }

    /**
     * Whether the model should be transparent
     */
    public var transparent: Boolean = false

    /**
     * Whether the block should have a facing property
     */
    public var directional: Boolean = false

    public val rightClick: SidedAction<RightClickContext> = SidedAction()
    public val leftClick: SidedAction<LeftClickContext> = SidedAction()
    public val destroy: SidedAction<DestroyContext> = SidedAction()
    public val place: SidedAction<PlaceContext> = SidedAction()

//    internal var tileConfig: TestTileConfig<*>? = null
//
//    public fun <T: BlockEntity> tile(factory: (BlockEntityType<T>) -> T): TestTileConfig<T> {
//        val config = TestTileConfig(factory)
//        tileConfig = config
//        return config
//    }

    internal val instance: TestBlockImpl by lazy {
        TestBlockImpl(this)
    }
    internal val itemInstance: TestBlockItem by lazy {
        TestBlockItem(instance, Item.Settings().group(manager.itemGroup))
    }

    override fun registerCommon(resources: TestModResourceManager) {
        Registry.register(Registry.BLOCK, id, instance)
        Registry.register(Registry.ITEM, id, itemInstance)
    }

    override fun registerClient(resources: TestModResourceManager) {
        resources.lang.block(id, name)
        description?.also {
            resources.lang.block(id.append(".tooltip"), it)
        }
    }

    public data class RightClickContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity, val hand: Hand, val hit: BlockHitResult
    ): PlayerTestContext(player) {
        val stack: ItemStack = player.getStackInHand(hand)
    }

    public data class LeftClickContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity
    ): PlayerTestContext(player) {
    }

    public data class DestroyContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity
    ): PlayerTestContext(player) {
    }

    public data class PlaceContext(
        val state: BlockState, val world: World, val pos: BlockPos,
        val player: PlayerEntity, val stack: ItemStack
    ): PlayerTestContext(player) {
    }

    private companion object {
        val testMaterial: Material = Material(
            MaterialColor.PINK, // materialMapColorIn
            false, // liquid
            false, // solid
            true, // doesBlockMovement
            false, // opaque
            false, // canBurnIn
            false, // replaceableIn
            PistonBehavior.NORMAL // mobilityFlag
        )
    }
}