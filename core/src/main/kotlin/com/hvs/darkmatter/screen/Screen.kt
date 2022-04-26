package com.hvs.darkmatter.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import com.hvs.darkmatter.DarkMatter
import com.hvs.darkmatter.GameEventManager
import com.hvs.darkmatter.audio.AudioService
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage

abstract class Screen(
    val game: DarkMatter,
    private val gameViewPort: Viewport = game.gameViewPort,
    private val uiViewport: Viewport = game.uiViewport,
    val gameEventManager: GameEventManager = game.gameEventManager,
    val assets : AssetStorage = game.assets,
    val audioService: AudioService = game.audioService,
    val preferences: Preferences = game.preferences

): KtxScreen {

    override fun resize(width: Int, height: Int) {
        gameViewPort.update(width, height, true)
        uiViewport.update(width, height, true)
    }
}
