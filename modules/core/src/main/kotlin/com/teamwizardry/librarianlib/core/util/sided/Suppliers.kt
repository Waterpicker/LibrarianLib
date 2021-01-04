package com.teamwizardry.librarianlib.core.util.sided

import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import java.util.function.Supplier

/**
 * A supplier that will get different blocks of code on the client and server.
 */
public interface SidedSupplier<T>: Supplier<T> {
    override fun get(): T {
        return when (FabricLoader.getInstance().environmentType) {
            EnvType.CLIENT -> getClient()
            EnvType.SERVER -> getServer()
            null -> inconceivable("No dist")
        }
    }

    @Environment(EnvType.CLIENT)
    public fun getClient(): T

    @Environment(EnvType.SERVER)
    public fun getServer(): T

    public companion object {
        /**
         * Runs a block of code only on the client.
         */
        @JvmStatic
        public fun <T> client(supplier: ClientSupplier<T>): T? {
            return supplier.get()
        }

        /**
         * Runs a block of code only on the server.
         */
        @JvmStatic
        public fun <T> server(supplier: ServerSupplier<T>): T? {
            return supplier.get()
        }

        /**
         * Runs different blocks of code on the client and the server.
         */
        @JvmStatic
        public fun <T> sided(client: ClientSupplier<T>, server: ServerSupplier<T>): T {
            return object: SidedSupplier<T> {
                override fun getClient(): T {
                    return client.getClient()
                }

                override fun getServer(): T {
                    return server.getServer()
                }
            }.get()
        }
    }
}

/**
 * A supplier that will get only on the client.
 */
public fun interface ClientSupplier<T>: Supplier<T?> {
    override fun get(): T? {
        return if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            getClient()
        } else {
            null
        }
    }

    @Environment(EnvType.CLIENT)
    public fun getClient(): T
}

/**
 * A supplier that will get only on the server.
 */
public fun interface ServerSupplier<T>: Supplier<T?> {
    override fun get(): T? {
        return if (FabricLoader.getInstance().environmentType == EnvType.SERVER) {
            getServer()
        } else {
            null
        }
    }

    @Environment(EnvType.SERVER)
    public fun getServer(): T
}
