package com.teamwizardry.librarianlib.core.util

import net.minecraft.client.render.RenderPhase

public object DefaultRenderStates {
    public val NO_TRANSPARENCY: RenderPhase.Transparency = RenderStateAccess._NO_TRANSPARENCY
    public val ADDITIVE_TRANSPARENCY: RenderPhase.Transparency = RenderStateAccess._ADDITIVE_TRANSPARENCY
    public val LIGHTNING_TRANSPARENCY: RenderPhase.Transparency = RenderStateAccess._LIGHTNING_TRANSPARENCY
    public val GLINT_TRANSPARENCY: RenderPhase.Transparency = RenderStateAccess._GLINT_TRANSPARENCY
    public val CRUMBLING_TRANSPARENCY: RenderPhase.Transparency = RenderStateAccess._CRUMBLING_TRANSPARENCY
    public val TRANSLUCENT_TRANSPARENCY: RenderPhase.Transparency = RenderStateAccess._TRANSLUCENT_TRANSPARENCY
    public val ZERO_ALPHA: RenderPhase.Alpha = RenderStateAccess._ZERO_ALPHA
    public val DEFAULT_ALPHA: RenderPhase.Alpha = RenderStateAccess._DEFAULT_ALPHA
    public val HALF_ALPHA: RenderPhase.Alpha = RenderStateAccess._HALF_ALPHA
    public val SHADE_DISABLED: RenderPhase.ShadeModel = RenderStateAccess._SHADE_DISABLED
    public val SHADE_ENABLED: RenderPhase.ShadeModel = RenderStateAccess._SHADE_ENABLED
    public val BLOCK_SHEET_MIPPED: RenderPhase.Texture = RenderStateAccess._BLOCK_SHEET_MIPPED
    public val BLOCK_SHEET: RenderPhase.Texture = RenderStateAccess._BLOCK_SHEET
    public val NO_TEXTURE: RenderPhase.Texture = RenderStateAccess._NO_TEXTURE
    public val DEFAULT_TEXTURING: RenderPhase.Texturing = RenderStateAccess._DEFAULT_TEXTURING
    public val OUTLINE_TEXTURING: RenderPhase.Texturing = RenderStateAccess._OUTLINE_TEXTURING
    public val GLINT_TEXTURING: RenderPhase.Texturing = RenderStateAccess._GLINT_TEXTURING
    public val ENTITY_GLINT_TEXTURING: RenderPhase.Texturing = RenderStateAccess._ENTITY_GLINT_TEXTURING
    public val LIGHTMAP_ENABLED: RenderPhase.Lightmap = RenderStateAccess._LIGHTMAP_ENABLED
    public val LIGHTMAP_DISABLED: RenderPhase.Lightmap = RenderStateAccess._LIGHTMAP_DISABLED
    public val OVERLAY_ENABLED: RenderPhase.Overlay = RenderStateAccess._OVERLAY_ENABLED
    public val OVERLAY_DISABLED: RenderPhase.Overlay = RenderStateAccess._OVERLAY_DISABLED
    public val DIFFUSE_LIGHTING_ENABLED: RenderPhase.DiffuseLighting = RenderStateAccess._DIFFUSE_LIGHTING_ENABLED
    public val DIFFUSE_LIGHTING_DISABLED: RenderPhase.DiffuseLighting = RenderStateAccess._DIFFUSE_LIGHTING_DISABLED
    public val CULL_ENABLED: RenderPhase.Cull = RenderStateAccess._CULL_ENABLED
    public val CULL_DISABLED: RenderPhase.Cull = RenderStateAccess._CULL_DISABLED
    public val DEPTH_ALWAYS: RenderPhase.DepthTest = RenderStateAccess._DEPTH_ALWAYS
    public val DEPTH_EQUAL: RenderPhase.DepthTest = RenderStateAccess._DEPTH_EQUAL
    public val DEPTH_LEQUAL: RenderPhase.DepthTest = RenderStateAccess._DEPTH_LEQUAL
    public val COLOR_DEPTH_WRITE: RenderPhase.WriteMaskState = RenderStateAccess._COLOR_DEPTH_WRITE
    public val COLOR_WRITE: RenderPhase.WriteMaskState = RenderStateAccess._COLOR_WRITE
    public val DEPTH_WRITE: RenderPhase.WriteMaskState = RenderStateAccess._DEPTH_WRITE
    public val NO_LAYERING: RenderPhase.Layering = RenderStateAccess._NO_LAYERING
    public val POLYGON_OFFSET_LAYERING: RenderPhase.Layering = RenderStateAccess._POLYGON_OFFSET_LAYERING
    public val PROJECTION_LAYERING: RenderPhase.Layering = RenderStateAccess._PROJECTION_LAYERING
    public val NO_FOG: RenderPhase.Fog = RenderStateAccess._NO_FOG
    public val FOG: RenderPhase.Fog = RenderStateAccess._FOG
    public val BLACK_FOG: RenderPhase.Fog = RenderStateAccess._BLACK_FOG
    public val MAIN_TARGET: RenderPhase.Target = RenderStateAccess._MAIN_TARGET
    public val OUTLINE_TARGET: RenderPhase.Target = RenderStateAccess._OUTLINE_TARGET
    public val DEFAULT_LINE: RenderPhase.LineWidth = RenderStateAccess._DEFAULT_LINE
}

@Suppress("ObjectPropertyName")
private object RenderStateAccess: RenderPhase("", Runnable {}, Runnable {}) {
    val _NO_TRANSPARENCY: Transparency = NO_TRANSPARENCY
    val _ADDITIVE_TRANSPARENCY: Transparency = ADDITIVE_TRANSPARENCY
    val _LIGHTNING_TRANSPARENCY: Transparency = LIGHTNING_TRANSPARENCY
    val _GLINT_TRANSPARENCY: Transparency = GLINT_TRANSPARENCY
    val _CRUMBLING_TRANSPARENCY: Transparency = CRUMBLING_TRANSPARENCY
    val _TRANSLUCENT_TRANSPARENCY: Transparency = TRANSLUCENT_TRANSPARENCY
    val _ZERO_ALPHA: Alpha = ZERO_ALPHA
    val _DEFAULT_ALPHA: Alpha = ONE_TENTH_ALPHA
    val _HALF_ALPHA: Alpha = HALF_ALPHA
    val _SHADE_DISABLED: ShadeModel = SHADE_MODEL
    val _SHADE_ENABLED: ShadeModel = SMOOTH_SHADE_MODEL
    val _BLOCK_SHEET_MIPPED: Texture = MIPMAP_BLOCK_ATLAS_TEXTURE
    val _BLOCK_SHEET: Texture = BLOCK_ATLAS_TEXTURE
    val _NO_TEXTURE: Texture = NO_TEXTURE
    val _DEFAULT_TEXTURING: Texturing = DEFAULT_TEXTURING
    val _OUTLINE_TEXTURING: Texturing = OUTLINE_TEXTURING
    val _GLINT_TEXTURING: Texturing = GLINT_TEXTURING
    val _ENTITY_GLINT_TEXTURING: Texturing = ENTITY_GLINT_TEXTURING
    val _LIGHTMAP_ENABLED: Lightmap = ENABLE_LIGHTMAP
    val _LIGHTMAP_DISABLED: Lightmap = DISABLE_LIGHTMAP
    val _OVERLAY_ENABLED: Overlay = ENABLE_OVERLAY_COLOR
    val _OVERLAY_DISABLED: Overlay = DISABLE_OVERLAY_COLOR
    val _DIFFUSE_LIGHTING_ENABLED: DiffuseLighting = ENABLE_DIFFUSE_LIGHTING
    val _DIFFUSE_LIGHTING_DISABLED: DiffuseLighting = DISABLE_DIFFUSE_LIGHTING
    val _CULL_ENABLED: Cull = ENABLE_CULLING
    val _CULL_DISABLED: Cull = DISABLE_CULLING
    val _DEPTH_ALWAYS: DepthTest = ALWAYS_DEPTH_TEST
    val _DEPTH_EQUAL: DepthTest = EQUAL_DEPTH_TEST
    val _DEPTH_LEQUAL: DepthTest = LEQUAL_DEPTH_TEST
    val _COLOR_DEPTH_WRITE: WriteMaskState = ALL_MASK
    val _COLOR_WRITE: WriteMaskState = COLOR_MASK
    val _DEPTH_WRITE: WriteMaskState = DEPTH_MASK
    val _NO_LAYERING: Layering = NO_LAYERING
    val _POLYGON_OFFSET_LAYERING: Layering = POLYGON_OFFSET_LAYERING
    val _PROJECTION_LAYERING: Layering = VIEW_OFFSET_Z_LAYERING
    val _NO_FOG: Fog = NO_FOG
    val _FOG: Fog = FOG
    val _BLACK_FOG: Fog = BLACK_FOG
    val _MAIN_TARGET: Target = MAIN_TARGET
    val _OUTLINE_TARGET: Target = OUTLINE_TARGET
    val _DEFAULT_LINE: LineWidth = FULL_LINE_WIDTH
}
