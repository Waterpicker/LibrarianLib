package com.teamwizardry.librarianlib.features.base

import com.teamwizardry.librarianlib.features.base.block.IModBlockProvider
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Provides a list of variants to load. The most basic class which can be put into the ModelHandler.
 */
interface IVariantHolder {

    /**
     * The variants which will be loaded as models. In all implementations these should correspond to metadata values of items.
     * If the mesh definition is not null, none of these will be loaded as models. It's recommended in such situations to use IExtraVariantHolder.
     */
    val variants: Array<out String>
}

/**
 * Provides an extra list of variants to load, without adding extra metadata variants.
 */
interface IExtraVariantHolder : IVariantHolder {

    /**
     * Extra variants to load as models without corresponding to metadata.
     */
    val extraVariants: Array<out String>
}

/**
 * Internal, do not use unless you know what you're doing.
 */
interface IModelGenerator : IVariantHolder {

    fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean = false

    fun generateMissingItem(item: IModItemProvider, variant: String): Boolean = false
}

/**
 * Implement this interface to provide a custom default texture path for automatic json model generation.
 */
interface ICustomTexturePath {
    fun texturePath(variant: String): String
}
