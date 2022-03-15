package com.hvs.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.viewport.FitViewport
import com.hvs.darkmatter.DarkMatter
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger

class GameScreen(
    private val game: DarkMatter,
    private val batch: Batch
) : Screen(game) {

    private val viewPort = FitViewport(9f, 16f)
    private val texture = Texture(Gdx.files.internal("raw/graphics/ship_base.png"))
    private val sprite = Sprite(texture).apply { setSize(1f , 1f )}

    override fun show() {
        LOG.debug { "Second screen is shown" }
        sprite.setPosition(1f, 1f)
    }

    override fun resize(width: Int, height: Int) {
        viewPort.update(width, height, true)
    }

    override fun render(delta: Float) {
        viewPort.apply()
        batch.use(viewPort.camera.combined) {
            sprite.draw(it)
        }
    }

    override fun dispose() {
        texture.dispose()
    }

    companion object {
        private val LOG = logger<GameScreen>()
    }
}
