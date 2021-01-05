package com.teamwizardry.librarianlib.prism.nbt

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.nbt.INBT
import net.minecraft.nbt.Tag
import net.minecraftforge.common.util.INBTSerializable

internal class INBTSerializableSerializerFactory(prism: NBTPrism): NBTSerializerFactory(prism, Mirror.reflect<INBTSerializable<*>>()) {
    override fun create(mirror: TypeMirror): NBTSerializer<*> {
        return INBTSerializableSerializer(mirror as ClassMirror)
    }

    class INBTSerializableSerializer(type: ClassMirror): NBTSerializer<INBTSerializable<Tag>>(type) {
        override fun deserialize(tag: Tag, existing: INBTSerializable<Tag>?): INBTSerializable<Tag> {
            if(existing == null)
                throw DeserializationException("INBTSerializable requires an existing value to deserialize")
            existing.deserializeNBT(tag)
            return existing
        }

        override fun serialize(value: INBTSerializable<Tag>): Tag {
            return value.serializeNBT()
        }
    }
}
