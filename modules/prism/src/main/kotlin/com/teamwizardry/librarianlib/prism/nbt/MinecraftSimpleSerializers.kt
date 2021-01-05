package com.teamwizardry.librarianlib.prism.nbt

import com.mojang.authlib.GameProfile
import com.teamwizardry.librarianlib.core.util.block
import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.block.BlockState
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.ItemStack
import net.minecraft.util.Pair
import net.minecraft.nbt.*
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.GlobalPos
import net.minecraft.util.math.*
import net.minecraft.world.dimension.DimensionType
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.registry.GameRegistry

internal object IdentifierSerializer: NBTSerializer<Identifier>() {
    override fun deserialize(tag: Tag, existing: Identifier?): Identifier {
        return Identifier(tag.expectType<StringTag>("tag").asString())
    }

    override fun serialize(value: Identifier): Tag {
        return StringTag.of(value.toString())
    }
}

//region Math stuff

internal object Vec3dSerializer: NBTSerializer<Vec3d>() {
    override fun deserialize(tag: Tag, existing: Vec3d?): Vec3d {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return Vec3d(
            tag.expect<AbstractNumberTag>("X").double,
            tag.expect<AbstractNumberTag>("Y").double,
            tag.expect<AbstractNumberTag>("Z").double
        )
    }

    override fun serialize(value: Vec3d): Tag {
        val tag = CompoundTag()
        tag.put("X", DoubleTag.of(value.x))
        tag.put("Y", DoubleTag.of(value.y))
        tag.put("Z", DoubleTag.of(value.z))
        return tag
    }
}

internal object Vec2fSerializer: NBTSerializer<Vec2f>() {
    override fun deserialize(tag: Tag, existing: Vec2f?): Vec2f {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return Vec2f(
            tag.expect<AbstractNumberTag>("X").float,
            tag.expect<AbstractNumberTag>("Y").float
        )
    }

    override fun serialize(value: Vec2f): Tag {
        val tag = CompoundTag()
        tag.put("X", FloatTag.of(value.x))
        tag.put("Y", FloatTag.of(value.y))
        return tag
    }
}

internal object BlockPosSerializer: NBTSerializer<BlockPos>() {
    override fun deserialize(tag: Tag, existing: BlockPos?): BlockPos {
        return NbtHelper.toBlockPos(tag.expectType("tag"))
    }

    override fun serialize(value: BlockPos): Tag {
        return NbtHelper.fromBlockPos(value)
    }
}


internal object ChunkPosSerializer: NBTSerializer<ChunkPos>() {
    override fun deserialize(tag: Tag, existing: ChunkPos?): ChunkPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return ChunkPos(
            tag.expect<AbstractNumberTag>("X").int,
            tag.expect<AbstractNumberTag>("Z").int
        )
    }

    override fun serialize(value: ChunkPos): Tag {
        val tag = CompoundTag()
        tag.put("X", IntTag.of(value.x))
        tag.put("Z", IntTag.of(value.z))
        return tag
    }
}

internal object ColumnPosSerializer: NBTSerializer<ColumnPos>() {
    override fun deserialize(tag: Tag, existing: ColumnPos?): ColumnPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return ColumnPos(
            tag.expect<AbstractNumberTag>("X").int,
            tag.expect<AbstractNumberTag>("Z").int
        )
    }

    override fun serialize(value: ColumnPos): Tag {
        val tag = CompoundTag()
        tag.put("X", IntTag.of(value.x))
        tag.put("Z", IntTag.of(value.z))
        return tag
    }
}

internal object SectionPosSerializer: NBTSerializer<ChunkSectionPos>() {
    override fun deserialize(tag: Tag, existing: ChunkSectionPos?): ChunkSectionPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return ChunkSectionPos.from(
            ChunkPos(
                tag.expect<AbstractNumberTag>("X").int,
                tag.expect<AbstractNumberTag>("Z").int
            ),
            tag.expect<AbstractNumberTag>("Y").int
        )
    }

    override fun serialize(value: ChunkSectionPos): Tag {
        val tag = CompoundTag()
        tag.put("X", IntTag.of(value.x))
        tag.put("Y", IntTag.of(value.y))
        tag.put("Z", IntTag.of(value.z))
        return tag
    }
}

internal object GlobalPosSerializer: NBTSerializer<GlobalPos>() {
    override fun deserialize(tag: Tag, existing: GlobalPos?): GlobalPos {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        val dimensionName = Identifier(tag.expect<StringTag>("Dimension").asString())
        return GlobalPos.create(
                DimensionType.byName(dimensionName)!!, // `!!` because the dimension type registry has a default value
            block(
                tag.expect<AbstractNumberTag>("X").int,
                tag.expect<AbstractNumberTag>("Y").int,
                tag.expect<AbstractNumberTag>("Z").int
            )
        )
    }

    override fun serialize(value: GlobalPos): Tag {
        val tag = CompoundTag()
        tag.put("Dimension", StringTag.of(value.dimension.registryName.toString()))
        tag.put("X", IntTag.of(value.pos.x))
        tag.put("Y", IntTag.of(value.pos.y))
        tag.put("Z", IntTag.of(value.pos.z))
        return tag
    }
}

internal object RotationsSerializer: NBTSerializer<EulerAngle>() {
    override fun deserialize(tag: Tag, existing: EulerAngle?): EulerAngle {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return EulerAngle(
            tag.expect<AbstractNumberTag>("Pitch").float,
            tag.expect<AbstractNumberTag>("Yaw").float,
            tag.expect<AbstractNumberTag>("Roll").float
        )
    }

    override fun serialize(value: EulerAngle): Tag {
        val tag = CompoundTag()
        tag.put("Pitch", FloatTag.of(value.pitch))
        tag.put("Yaw", FloatTag.of(value.yaw))
        tag.put("Roll", FloatTag.of(value.roll))
        return tag
    }
}

internal object AxisAlignedBBSerializer: NBTSerializer<Box>() {
    override fun deserialize(tag: Tag, existing: Box?): Box {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return Box(
            tag.expect<AbstractNumberTag>("MinX").double,
            tag.expect<AbstractNumberTag>("MinY").double,
            tag.expect<AbstractNumberTag>("MinZ").double,
            tag.expect<AbstractNumberTag>("MaxX").double,
            tag.expect<AbstractNumberTag>("MaxY").double,
            tag.expect<AbstractNumberTag>("MaxZ").double
        )
    }

    override fun serialize(value: Box): Tag {
        val tag = CompoundTag()
        tag.put("MinX", DoubleTag.of(value.minX))
        tag.put("MinY", DoubleTag.of(value.minY))
        tag.put("MinZ", DoubleTag.of(value.minZ))
        tag.put("MaxX", DoubleTag.of(value.maxX))
        tag.put("MaxY", DoubleTag.of(value.maxY))
        tag.put("MaxZ", DoubleTag.of(value.maxZ))
        return tag
    }
}

internal object MutableBoundingBoxSerializer: NBTSerializer<BlockBox>() {
    override fun deserialize(tag: Tag, existing: BlockBox?): BlockBox {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return BlockBox(
            tag.expect<AbstractNumberTag>("MinX").int,
            tag.expect<AbstractNumberTag>("MinY").int,
            tag.expect<AbstractNumberTag>("MinZ").int,
            tag.expect<AbstractNumberTag>("MaxX").int,
            tag.expect<AbstractNumberTag>("MaxY").int,
            tag.expect<AbstractNumberTag>("MaxZ").int
        )
    }

    override fun serialize(value: BlockBox): Tag {
        val tag = CompoundTag()
        tag.put("MinX", IntTag.of(value.minX))
        tag.put("MinY", IntTag.of(value.minY))
        tag.put("MinZ", IntTag.of(value.minZ))
        tag.put("MaxX", IntTag.of(value.maxX))
        tag.put("MaxY", IntTag.of(value.maxY))
        tag.put("MaxZ", IntTag.of(value.maxZ))
        return tag
    }
}

//endregion

internal class TupleSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Pair<*, *>>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return TupleSerializer(prism, mirror as ClassMirror)
    }

    class TupleSerializer(prism: NBTPrism, type: ClassMirror): NBTSerializer<Pair<Any?, Any?>>(type) {
        private val firstSerializer by prism[type.typeParameters[0]]
        private val secondSerializer by prism[type.typeParameters[1]]

        override fun deserialize(tag: Tag, existing: Pair<Any?, Any?>?): Pair<Any?, Any?> {
            @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") // stupid @MethodsReturnNonnullByDefault
            return Pair(
                if (tag.contains("Left")) firstSerializer.read(tag.expect("Left"), existing?.right) else null,
                if (tag.contains("Right")) secondSerializer.read(tag.expect("Right), existing?.) else null
            )
        }

        override fun serialize(value: Pair<Any?, Any?>): Tag {
            val tag = CompoundTag()
            @Suppress("UNNECESSARY_SAFE_CALL") // stupid @MethodsReturnNonnullByDefault
            value.left?.also { tag.put("Left", firstSerializer.write(it)) }
            @Suppress("UNNECESSARY_SAFE_CALL") // stupid @MethodsReturnNonnullByDefault
            value.right?.also { tag.put("Right", secondSerializer.write(it)) }
            return tag
        }
    }
}

internal class TagPassthroughSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Tag>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return TagPassthroughSerializer(mirror as ClassMirror)
    }

    class TagPassthroughSerializer(type: ClassMirror): NBTSerializer<Tag>(type) {
        @Suppress("UNCHECKED_CAST")
        private val nbtClass = type.erasure as Class<Tag>

        override fun deserialize(tag: Tag, existing: Tag?): Tag {
            return expectType(tag, nbtClass, "tag").copy()
        }

        override fun serialize(value: Tag): Tag {
            return value.copy()
        }
    }
}

internal class ITextComponentSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Text>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return ITextComponentSerializer(mirror as ClassMirror)
    }

    class ITextComponentSerializer(type: ClassMirror): NBTSerializer<Text>(type) {
        private val componentClass = type.erasure

        override fun deserialize(tag: Tag, existing: Text?): Text {
            val component = Text.Serializer.fromJson(tag.expectType<StringTag>("tag").asString())
                ?: inconceivable("ITextComponent.Serializer.fromJson doesn't seem to ever return null")
            if(!componentClass.isAssignableFrom(component.javaClass))
                throw DeserializationException("Wrong ITextComponent type. Expected ${componentClass.simpleName}, " +
                    "found ${component.javaClass.simpleName}.")
            return component
        }

        override fun serialize(value: Text): Tag {
            return StringTag.of(Text.Serializer.toJson(value))
        }
    }
}

internal object GameProfileSerializer: NBTSerializer<GameProfile>() {
    override fun deserialize(tag: Tag, existing: GameProfile?): GameProfile {
        return NbtHelper.toGameProfile(tag.expectType("tag"))
            ?: throw DeserializationException("Reading GameProfile") // it only returns null if an error occurs
    }

    override fun serialize(value: GameProfile): Tag {
        val tag = CompoundTag()
        NbtHelper.fromGameProfile(tag, value)
        return tag
    }
}

internal object BlockStateSerializer: NBTSerializer<BlockState>() {
    override fun deserialize(tag: Tag, existing: BlockState?): BlockState {
        return NbtHelper.toBlockState(tag.expectType("tag"))
    }

    override fun serialize(value: BlockState): Tag {
        return NbtHelper.fromBlockState(value)
    }
}

internal object ItemStackSerializer: NBTSerializer<ItemStack>() {
    override fun deserialize(tag: Tag, existing: ItemStack?): ItemStack {
        return ItemStack.fromTag(tag.expectType("tag"))
    }

    override fun serialize(value: ItemStack): Tag {
        return value.toTag(CompoundTag())
    }
}

internal object FluidStackSerializer: NBTSerializer<FluidStack>() {
    override fun deserialize(tag: Tag, existing: FluidStack?): FluidStack {
        return FluidStack.loadFluidStackFromNBT(tag.expectType("tag"))
    }

    override fun serialize(value: FluidStack): Tag {
        return value.writeToNBT(CompoundTag())
    }
}

internal object EffectInstanceSerializer: NBTSerializer<StatusEffectInstance>() {
    override fun deserialize(tag: Tag, existing: StatusEffectInstance?): StatusEffectInstance {
        return StatusEffectInstance.fromTag(tag.expectType("tag"))
    }

    override fun serialize(value: StatusEffectInstance): Tag {
        return value.toTag(CompoundTag())
    }
}

internal object EnchantmentDataSerializer: NBTSerializer<EnchantmentLevelEntry>() {
    private val registry by lazy {
        GameRegistry.findRegistry(Enchantment::class.java)
    }

    override fun deserialize(tag: Tag, existing: EnchantmentLevelEntry?): EnchantmentLevelEntry {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        val dimensionName = Identifier(tag.expect<StringTag>("Enchantment").asString())
        val enchantment = registry.getValue(dimensionName)
            ?: throw DeserializationException("Unknown enchantment type $dimensionName")
        return EnchantmentLevelEntry(
            enchantment,
            tag.expect<AbstractNumberTag>("Level").int
        )
    }

    override fun serialize(value: EnchantmentLevelEntry): Tag {
        val tag = CompoundTag()
        tag.put("Enchantment", StringTag.of(value.enchantment.registryName.toString()))
        tag.put("Level", IntTag.of(value.level))
        return tag
    }
}
