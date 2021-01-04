package com.teamwizardry.librarianlib.core.util.sided

import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader

/**
 * A runnable that will run different blocks of code on the client and server.
 */
public interface SidedRunnable: Runnable {
    override fun run() {
        when (FabricLoader.getInstance().environmentType) {
            EnvType.CLIENT -> runClient()
            EnvType.SERVER -> runServer()
            null -> inconceivable("No dist")
        }
    }

    @Environment(EnvType.CLIENT)
    public fun runClient()

    @Environment(EnvType.SERVER)
    public fun runServer()

    public companion object {
        /**
         * Runs a block of code only on the client.
         */
        @JvmStatic
        public fun client(runnable: ClientRunnable) {
            runnable.run()
        }

        /**
         * Runs a block of code only on the server.
         */
        @JvmStatic
        public fun server(runnable: ServerRunnable) {
            runnable.run()
        }

        /**
         * Runs different blocks of code on the client and the server.
         */
        @JvmStatic
        public fun sided(clientRunnable: ClientRunnable, serverRunnable: ServerRunnable) {
            object: SidedRunnable {
                override fun runClient() {
                    clientRunnable.runClient()
                }

                override fun runServer() {
                    serverRunnable.runServer()
                }
            }.run()
        }
    }
}

/**
 * A runnable that will run only on the client.
 */
public fun interface ClientRunnable: Runnable {
    override fun run() {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            runClient()
        }
    }

    @Environment(EnvType.SERVER)
    public fun runClient()
}

/**
 * A runnable that will run only on the server.
 */
public fun interface ServerRunnable: Runnable {
    override fun run() {
        if (FabricLoader.getInstance().environmentType == EnvType.SERVER) {
            runServer()
        }
    }

    @Environment(EnvType.SERVER)
    public fun runServer()
}
