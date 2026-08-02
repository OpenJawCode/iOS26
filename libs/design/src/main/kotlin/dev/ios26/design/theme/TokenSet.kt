package dev.ios26.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import dev.ios26.design.tokens.Tokens

/** Theme modes — token-driven (semantic color sets, ADR-0011). */
enum class ThemeMode { Light, Dark }

/**
 * Glass intensity — the runtime-adjustable translucency dimension (research R3: iOS 27's
 * Liquid Glass slider analog). Maps to blur + tint pairs in the Glass Engine.
 */
enum class GlassIntensity { Subtle, Standard, Prominent }

/** Semantic token set — one per mode. Everything visual derives from this (nothing hardcoded). */
@Immutable
class TokenSet private constructor(
    val background: Color,
    val backgroundSecondary: Color,
    val backgroundTertiary: Color,
    val labelPrimary: Color,
    val labelSecondary: Color,
    val labelTertiary: Color,
    val labelQuaternary: Color,
    val separator: Color,
    val separatorOpaque: Color,
    val accent: Color,
    val glassFill: Color,
    val glassFillSecondary: Color,
    val glassStroke: Color,
    val glassHighlight: Color,
    val glassShadow: Color,
    val scrim: Color,
    val scrimLight: Color,
    val widgetTint: Color,
    val sheetBackground: Color,
) {
    companion object {
        fun light(): TokenSet = TokenSet(
            background = Tokens.Semantic.Light.background,
            backgroundSecondary = Tokens.Semantic.Light.backgroundSecondary,
            backgroundTertiary = Tokens.Semantic.Light.backgroundTertiary,
            labelPrimary = Tokens.Semantic.Light.labelPrimary,
            labelSecondary = Tokens.Semantic.Light.labelSecondary,
            labelTertiary = Tokens.Semantic.Light.labelTertiary,
            labelQuaternary = Tokens.Semantic.Light.labelQuaternary,
            separator = Tokens.Semantic.Light.separator,
            separatorOpaque = Tokens.Semantic.Light.separatorOpaque,
            accent = Tokens.Semantic.Light.accent,
            glassFill = Tokens.Semantic.Light.glassFill,
            glassFillSecondary = Tokens.Semantic.Light.glassFillSecondary,
            glassStroke = Tokens.Semantic.Light.glassStroke,
            glassHighlight = Tokens.Semantic.Light.glassHighlight,
            glassShadow = Tokens.Semantic.Light.glassShadow,
            scrim = Tokens.Semantic.Light.scrim,
            scrimLight = Tokens.Semantic.Light.scrimLight,
            widgetTint = Tokens.Semantic.Light.widgetTint,
            sheetBackground = Tokens.Semantic.Light.sheetBackground,
        )

        fun dark(): TokenSet = TokenSet(
            background = Tokens.Semantic.Dark.background,
            backgroundSecondary = Tokens.Semantic.Dark.backgroundSecondary,
            backgroundTertiary = Tokens.Semantic.Dark.backgroundTertiary,
            labelPrimary = Tokens.Semantic.Dark.labelPrimary,
            labelSecondary = Tokens.Semantic.Dark.labelSecondary,
            labelTertiary = Tokens.Semantic.Dark.labelTertiary,
            labelQuaternary = Tokens.Semantic.Dark.labelQuaternary,
            separator = Tokens.Semantic.Dark.separator,
            separatorOpaque = Tokens.Semantic.Dark.separatorOpaque,
            accent = Tokens.Semantic.Dark.accent,
            glassFill = Tokens.Semantic.Dark.glassFill,
            glassFillSecondary = Tokens.Semantic.Dark.glassFillSecondary,
            glassStroke = Tokens.Semantic.Dark.glassStroke,
            glassHighlight = Tokens.Semantic.Dark.glassHighlight,
            glassShadow = Tokens.Semantic.Dark.glassShadow,
            scrim = Tokens.Semantic.Dark.scrim,
            scrimLight = Tokens.Semantic.Dark.scrimLight,
            widgetTint = Tokens.Semantic.Dark.widgetTint,
            sheetBackground = Tokens.Semantic.Dark.sheetBackground,
        )
    }
}
