package com.teamwizardry.librarianlib.prism.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import net.minecraft.nbt.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.BitSet
import java.util.UUID

internal class PairSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Pair<*, *>>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return PairSerializer(prism, mirror as ClassMirror)
    }

    class PairSerializer(prism: NBTPrism, type: ClassMirror): NBTSerializer<Pair<Any?, Any?>>(type) {
        private val firstSerializer by prism[type.typeParameters[0]]
        private val secondSerializer by prism[type.typeParameters[1]]

        override fun deserialize(tag: Tag, existing: Pair<Any?, Any?>?): Pair<Any?, Any?> {
            @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
            return Pair(
                if(tag.contains("First")) firstSerializer.read(tag.expect("First"), existing?.first) else null,
                if(tag.contains("Second")) secondSerializer.read(tag.expect("Second"), existing?.second) else null
            )
        }

        override fun serialize(value: Pair<Any?, Any?>): Tag {
            val tag = CompoundTag()
            value.first?.also { tag.put("First", firstSerializer.write(it)) }
            value.second?.also { tag.put("Second", secondSerializer.write(it)) }
            return tag
        }
    }
}

internal class TripleSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<Triple<*, *, *>>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return TripleSerializer(prism, mirror as ClassMirror)
    }

    class TripleSerializer(prism: NBTPrism, type: ClassMirror): NBTSerializer<Triple<Any?, Any?, Any?>>(type) {
        private val firstSerializer by prism[type.typeParameters[0]]
        private val secondSerializer by prism[type.typeParameters[1]]
        private val thirdSerializer by prism[type.typeParameters[2]]

        override fun deserialize(tag: Tag, existing: Triple<Any?, Any?, Any?>?): Triple<Any?, Any?, Any?> {
            @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
            return Triple(
                if(tag.contains("First")) firstSerializer.read(tag.expect("First"), existing?.first) else null,
                if(tag.contains("Second")) secondSerializer.read(tag.expect("Second"), existing?.second) else null,
                if(tag.contains("Third")) thirdSerializer.read(tag.expect("Third"), existing?.third) else null
            )
        }

        override fun serialize(value: Triple<Any?, Any?, Any?>): Tag {
            val tag = CompoundTag()
            value.first?.also { tag.put("First", firstSerializer.write(it)) }
            value.second?.also { tag.put("Second", secondSerializer.write(it)) }
            value.third?.also { tag.put("Third", thirdSerializer.write(it)) }
            return tag
        }
    }
}

internal object BigIntegerSerializer: NBTSerializer<BigInteger>() {
    override fun deserialize(tag: Tag, existing: BigInteger?): BigInteger {
        return BigInteger(tag.expectType<ByteArrayTag>("tag").byteArray)
    }

    override fun serialize(value: BigInteger): Tag {
        return ByteArrayTag(value.toByteArray())
    }
}

internal object BigDecimalSerializer: NBTSerializer<BigDecimal>() {
    override fun deserialize(tag: Tag, existing: BigDecimal?): BigDecimal {
        @Suppress("NAME_SHADOWING") val tag = tag.expectType<CompoundTag>("tag")
        return BigDecimal(
            BigInteger(tag.expect<ByteArrayTag>("Value").byteArray),
            tag.expect<AbstractNumberTag>("Scale").int
        )
    }

    override fun serialize(value: BigDecimal): Tag {
        return CompoundTag().also {
            it.put("Value", ByteArrayTag(value.unscaledValue().toByteArray()))
            it.put("Scale", IntTag.of(value.scale()))
        }
    }
}

internal object BitSetSerializer: NBTSerializer<BitSet>() {
    override fun deserialize(tag: Tag, existing: BitSet?): BitSet {
        val bitset = BitSet.valueOf(tag.expectType<ByteArrayTag>("tag").byteArray)
        return existing?.also {
            it.clear()
            it.or(bitset)
        } ?: bitset
    }

    override fun serialize(value: BitSet): Tag {
        return ByteArrayTag(value.toByteArray())
    }
}

internal object UUIDSerializer: NBTSerializer<UUID>() {
    override fun deserialize(tag: Tag, existing: UUID?): UUID {
        return NbtHelper.toUuid(tag.expectType("tag"))
    }

    override fun serialize(value: UUID): Tag {
        return NbtHelper.fromUuid(value)
    }
}

