package com.teamwizardry.librarianlib

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.Configurator

public abstract class LibrarianLibModule(public val name: String, public val humanName: String) {
    /**
     * Whether debugging is enabled for this module.
     */
    public var debugEnabled: Boolean = name in System.getProperty("librarianlib.debug.modules", "").split(",")
        private set

    /**
     * Loggers to update based on the debug flag.
     */
    private val registeredLoggers = mutableListOf<Logger>()
    private val modLoggers = mutableMapOf<String?, Logger>()

    public fun enableDebugging() {
        debugEnabled = true

        modLoggers.forEach { (_, logger) ->
            Configurator.setLevel(logger.name, Level.DEBUG)
        }
    }

    /**
     * Create a logger for this module.
     */
    public fun makeLogger(clazz: Class<*>): Logger {
        return makeLogger(clazz.simpleName)
    }

    /**
     * Create a logger for this module.
     */
    public inline fun <reified T> makeLogger(): Logger {
        return makeLogger(T::class.java)
    }

    /**
     * Create a logger for this module.
     */
    public fun makeLogger(label: String?): Logger {
        return modLoggers.getOrPut(label) {
            val labelSuffix = label?.let { " ($it)" } ?: ""
            val logger = LogManager.getLogger("LibrarianLib: $humanName$labelSuffix")
            registerLogger(logger)
            logger
        }
    }

    /**
     * Registers a logger which should be controlled by this module's debug flag
     */
    public fun registerLogger(logger: Logger) {
        if(debugEnabled)
            Configurator.setLevel(logger.name, Level.DEBUG)
        else
            Configurator.setLevel(logger.name, Level.INFO)
        registeredLoggers.add(logger)
    }
}
