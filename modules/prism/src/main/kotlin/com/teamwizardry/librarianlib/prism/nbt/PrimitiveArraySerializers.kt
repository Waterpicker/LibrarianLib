package com.teamwizardry.librarianlib.prism.nbt

import net.minecraft.nbt.*

internal object PrimitiveDoubleArraySerializer: NBTSerializer<DoubleArray>() {
    override fun deserialize(tag: Tag, existing: DoubleArray?): DoubleArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ListTag>("tag")
        val array = if(tag.size == existing?.size) existing else DoubleArray(tag.size)
        tag.forEachIndexed { index, inbt ->
            array[index] = inbt.expectType<AbstractNumberTag>("index $index").double
        }
        return array
    }

    override fun serialize(value: DoubleArray): Tag {
        return value.mapTo(ListTag()) { DoubleTag.of(it) }
    }
}

internal object PrimitiveFloatArraySerializer: NBTSerializer<FloatArray>() {
    override fun deserialize(tag: Tag, existing: FloatArray?): FloatArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ListTag>("tag")
        val array = if(tag.size == existing?.size) existing else FloatArray(tag.size)
        tag.forEachIndexed { index, inbt ->
            array[index] = inbt.expectType<AbstractNumberTag>("index $index").float
        }
        return array
    }

    override fun serialize(value: FloatArray): Tag {
        return value.mapTo(ListTag()) { FloatTag.of(it) }
    }
}

internal object PrimitiveLongArraySerializer: NBTSerializer<LongArray>() {
    override fun deserialize(tag: Tag, existing: LongArray?): LongArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<LongArrayTag>("tag")
        if(tag.longArray.size == existing?.size) {
            tag.longArray.copyInto(existing)
            return existing
        } else {
            return tag.longArray
        }
    }

    override fun serialize(value: LongArray): Tag {
        return LongArrayTag(value)
    }
}

internal object PrimitiveIntArraySerializer: NBTSerializer<IntArray>() {
    override fun deserialize(tag: Tag, existing: IntArray?): IntArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<IntArrayTag>("tag")
        if(tag.intArray.size == existing?.size) {
            tag.intArray.copyInto(existing)
            return existing
        } else {
            return tag.intArray
        }
    }

    override fun serialize(value: IntArray): Tag {
        return IntArrayTag(value)
    }
}

internal object PrimitiveShortArraySerializer: NBTSerializer<ShortArray>() {
    override fun deserialize(tag: Tag, existing: ShortArray?): ShortArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<IntArrayTag>("tag")
        if(tag.intArray.size == existing?.size) {
            tag.intArray.forEachIndexed { index, value ->
                existing[index] = value.toShort()
            }
            return existing
        } else {
            return ShortArray(tag.intArray.size) { tag.intArray[it].toShort() }
        }
    }

    override fun serialize(value: ShortArray): Tag {
        return IntArrayTag(IntArray(value.size) { value[it].toInt() })
    }
}

internal object PrimitiveCharArraySerializer: NBTSerializer<CharArray>() {
    override fun deserialize(tag: Tag, existing: CharArray?): CharArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<IntArrayTag>("tag")
        if(tag.intArray.size == existing?.size) {
            tag.intArray.forEachIndexed { index, value ->
                existing[index] = value.toChar()
            }
            return existing
        } else {
            return CharArray(tag.intArray.size) { tag.intArray[it].toChar() }
        }
    }

    override fun serialize(value: CharArray): Tag {
        return IntArrayTag(IntArray(value.size) { value[it].toInt() })
    }
}

internal object PrimitiveByteArraySerializer: NBTSerializer<ByteArray>() {
    override fun deserialize(tag: Tag, existing: ByteArray?): ByteArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ByteArrayTag>("tag")
        if(tag.byteArray.size == existing?.size) {
            tag.byteArray.copyInto(existing)
            return existing
        } else {
            return tag.byteArray
        }
    }

    override fun serialize(value: ByteArray): Tag {
        return ByteArrayTag(value)
    }
}

internal object PrimitiveBooleanArraySerializer: NBTSerializer<BooleanArray>() {
    override fun deserialize(tag: Tag, existing: BooleanArray?): BooleanArray {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<ByteArrayTag>("tag")
        if(tag.byteArray.size == existing?.size) {
            tag.byteArray.forEachIndexed { index, value ->
                existing[index] = value != 0.toByte()
            }
            return existing
        } else {
            return BooleanArray(tag.byteArray.size) { tag.byteArray[it] != 0.toByte() }
        }
    }

    override fun serialize(value: BooleanArray): Tag {
        return ByteArrayTag(ByteArray(value.size) { if(value[it]) 1.toByte() else 0.toByte() })
    }
}
