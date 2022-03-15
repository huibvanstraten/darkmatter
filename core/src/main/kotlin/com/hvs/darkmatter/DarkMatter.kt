package com.hvs.darkmatter

import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.hvs.darkmatter.screen.GameScreen
import com.hvs.darkmatter.screen.Screen
import ktx.app.KtxGame

class DarkMatter: KtxGame<Screen>() {

    val batch: Batch by lazy { SpriteBatch() }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        addScreen(GameScreen(this, this.batch))
        setScreen<GameScreen>()
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
    }

    companion object {
        const val UNIT_SCALE = 1 / 16f
    }
}