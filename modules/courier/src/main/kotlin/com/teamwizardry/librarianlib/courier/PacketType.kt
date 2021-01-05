package com.teamwizardry.librarianlib.courier

import net.minecraft.network.PacketBuffer
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

public abstract class PacketType<T>(public val type: Class<T>) {
    @get:JvmSynthetic
    @set:JvmSynthetic
    internal var index: Int = 0

    public abstract fun encode(packet: T, buffer: PacketByteBuf)
    public abstract fun decode(buffer: PacketByteBuf): T
    public abstract fun handle(packet: T, context: Supplier<NetworkEvent.Context>)
}