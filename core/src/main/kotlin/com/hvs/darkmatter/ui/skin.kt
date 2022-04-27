package com.hvs.darkmatter.ui

import com.hvs.darkmatter.ecs.asset.BitmapFontAsset
import com.hvs.darkmatter.ecs.asset.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.skin

fun createSkin(assetStorage: AssetStorage) {
    val atlas = assetStorage[TextureAtlasAsset.UI.descriptor]
    val gradientFont = assetStorage[BitmapFontAsset.FONT_LARGE_GRADIENT.descriptor]
    val normalFont = assetStorage[BitmapFontAsset.FONT_DEFAULT.descriptor]
    Scene2DSkin.defaultSkin = skin(atlas) {
        label("default") {
            font = normalFont
        }
        label("gradient") {
            font = gradientFont
        }
    }
}
