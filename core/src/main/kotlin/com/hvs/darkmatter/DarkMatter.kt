package com.hvs.darkmatter

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.hvs.darkmatter.ecs.system.RenderSystem
import com.hvs.darkmatter.screen.GameScreen
import com.hvs.darkmatter.screen.Screen
import ktx.app.KtxGame

class DarkMatter : KtxGame<Screen>() {
    val gameViewPort = FitViewport(9f, 16f)
    val batch: Batch by lazy { SpriteBatch() }
    val engine: Engine by lazy {
        PooledEngine().apply {
            addSystem(RenderSystem(batch, gameViewPort))
        }
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        addScreen(GameScreen(this))
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
