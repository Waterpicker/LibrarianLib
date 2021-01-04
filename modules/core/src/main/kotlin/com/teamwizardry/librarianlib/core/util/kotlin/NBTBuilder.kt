package com.teamwizardry.librarianlib.core.util.kotlin

import net.minecraft.nbt.*

@DslMarker
internal annotation class NBTBuilderDslMarker

@NBTBuilderDslMarker
public open class NBTBuilder @PublishedApi internal constructor() {
    public inline fun compound(block: CompoundNBTBuilder.() -> Unit): CompoundTag {
        val builder = CompoundNBTBuilder()
        builder.block()
        return builder.tag
    }

    public inline fun list(block: ListNBTBuilder.() -> Unit): ListTag {
        val builder = ListNBTBuilder()
        builder.block()
        return builder.tag
    }

    public inline fun list(vararg elements: Tag, block: ListNBTBuilder.() -> Unit): ListTag {
        val builder = ListNBTBuilder()
        builder.addAll(elements.toList())
        builder.block()
        return builder.tag
    }

    public fun list(vararg elements: Tag): ListTag {
        val tag = ListTag()
        tag.addAll(elements)
        return tag
    }

    public fun double(value: Int): DoubleTag = DoubleTag.of(value.toDouble())
    public fun double(value: Double): DoubleTag = DoubleTag.of(value)
    public fun float(value: Int): FloatTag = FloatTag.of(value.toFloat())
    public fun float(value: Float): FloatTag = FloatTag.of(value)
    public fun long(value: Int): LongTag = LongTag.of(value.toLong())
    public fun long(value: Long): LongTag = LongTag.of(value)
    public fun int(value: Int): IntTag = IntTag.of(value)
    public fun short(value: Int): ShortTag = ShortTag.of(value.toShort())
    public fun short(value: Short): ShortTag = ShortTag.of(value)
    public fun byte(value: Int): ByteTag = ByteTag.of(value.toByte())
    public fun byte(value: Byte): ByteTag = ByteTag.of(value)

    public fun string(value: String): StringTag = StringTag.of(value)

    public fun byteArray(vararg value: Int): ByteArrayTag = ByteArrayTag(value.map { it.toByte() }.toByteArray())
    public fun byteArray(vararg value: Byte): ByteArrayTag = ByteArrayTag(value)
    public fun byteArray(): ByteArrayTag = ByteArrayTag(byteArrayOf()) // avoiding overload ambiguity
    public fun longArray(vararg value: Int): LongArrayTag = LongArrayTag(value.map { it.toLong() }.toLongArray())
    public fun longArray(vararg value: Long): LongArrayTag = LongArrayTag(value)
    public fun longArray(): LongArrayTag = LongArrayTag(longArrayOf()) // avoiding overload ambiguity
    public fun intArray(vararg value: Int): IntArrayTag = IntArrayTag(value)

    public companion object: NBTBuilder()
}

public class CompoundNBTBuilder @PublishedApi internal constructor(): NBTBuilder() {
    public val tag: CompoundTag = CompoundTag()

    public operator fun String.timesAssign(nbt: Tag) {
        tag.put(this, nbt)
    }
}

public class ListNBTBuilder @PublishedApi internal constructor(): NBTBuilder() {
    public val tag: ListTag = ListTag()

    /**
     * A short alias for `this` which, in combination with [plus], allows syntax like `n+ SomeTag()`
     */
    public val n: ListNBTBuilder = this

    /**
     * Add the given NBT tag to this list
     */
    public operator fun plus(nbt: Tag) {
        this.tag.add(nbt)
    }

    /**
     * Add the given NBT tags to this list
     */
    public operator fun plus(nbt: Collection<Tag>) {
        this.tag.addAll(nbt)
    }

    /**
     * Add the given NBT tag to this list. This is explicitly defined for [ListTag] because otherwise there is overload
     * ambiguity between the [Tag] and [Collection]<[Tag]> methods.
     */
    public operator fun plus(nbt: ListTag) {
        this.tag.add(nbt)
    }

    public fun addAll(nbt: Collection<Tag>) {
        this.tag.addAll(nbt)
    }
    public fun add(nbt: Tag) {
        this.tag.add(nbt)
    }

    public fun doubles(vararg value: Int): List<DoubleTag> = value.map { DoubleTag.of(it.toDouble()) }
    public fun doubles(vararg value: Double): List<DoubleTag> = value.map { DoubleTag.of(it) }
    public fun floats(vararg value: Int): List<FloatTag> = value.map { FloatTag.of(it.toFloat()) }
    public fun floats(vararg value: Float): List<FloatTag> = value.map { FloatTag.of(it) }
    public fun longs(vararg value: Int): List<LongTag> = value.map { LongTag.of(it.toLong()) }
    public fun longs(vararg value: Long): List<LongTag> = value.map { LongTag.of(it) }
    public fun ints(vararg value: Int): List<IntTag> = value.map { IntTag.of(it) }
    public fun shorts(vararg value: Int): List<ShortTag> = value.map { ShortTag.of(it.toShort()) }
    public fun shorts(vararg value: Short): List<ShortTag> = value.map { ShortTag.of(it) }
    public fun bytes(vararg value: Int): List<ByteTag> = value.map { ByteTag.of(it.toByte()) }
    public fun bytes(vararg value: Byte): List<ByteTag> = value.map { ByteTag.of(it) }

    public fun strings(vararg value: String): List<StringTag> = value.map { StringTag.of(it) }
}
