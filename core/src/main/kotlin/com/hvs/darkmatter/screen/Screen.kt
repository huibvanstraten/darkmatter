package com.hvs.darkmatter.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import com.hvs.darkmatter.DarkMatter
import ktx.app.KtxScreen

abstract class Screen(
    val game: DarkMatter,
    val batch: Batch = game.batch,
    val gameViewPort: Viewport = game.gameViewPort,
    val engine: Engine = game.engine
): KtxScreen {

    override fun resize(width: Int, height: Int) {
        gameViewPort.update(width, height, true)
    }
}
