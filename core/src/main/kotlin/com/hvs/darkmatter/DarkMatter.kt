package com.hvs.darkmatter

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.FitViewport
import com.hvs.darkmatter.ecs.system.DamageSystem
import com.hvs.darkmatter.ecs.system.DebugSystem
import com.hvs.darkmatter.ecs.system.MoveSystem
import com.hvs.darkmatter.ecs.system.PlayerAnimationSystem
import com.hvs.darkmatter.ecs.system.PlayerInputSystem
import com.hvs.darkmatter.ecs.system.RemoveSystem
import com.hvs.darkmatter.ecs.system.RenderSystem
import com.hvs.darkmatter.screen.GameScreen
import com.hvs.darkmatter.screen.Screen
import ktx.app.KtxGame
import ktx.assets.dispose

class DarkMatter : KtxGame<Screen>() {
    val gameViewPort = FitViewport(VIRTUAL_WIDTH.toFloat(), VIRTUAL_HEIGHT.toFloat())
    val batch: Batch by lazy { SpriteBatch() }

    val graphicsAtlas by lazy{ TextureAtlas(Gdx.files.internal("assets/graphics/graphics.atlas")) }

    val engine: Engine by lazy {
        PooledEngine().apply {
            addSystem(PlayerInputSystem(gameViewPort))
            addSystem(MoveSystem())
            addSystem(DamageSystem())
            addSystem(PlayerAnimationSystem(
                graphicsAtlas.findRegion("ship_base"),
                graphicsAtlas.findRegion("ship_left"),
                graphicsAtlas.findRegion("ship_right")
            ))
            addSystem(RenderSystem(batch, gameViewPort))
            addSystem(RemoveSystem())
            addSystem(DebugSystem())
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

        graphicsAtlas.textures.dispose()
    }

    companion object {
        const val UNIT_SCALE = 1 / 16f
        const val VIRTUAL_WIDTH = 9
        const val VIRTUAL_HEIGHT = 16
    }
}
