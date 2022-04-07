package com.hvs.darkmatter.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.hvs.darkmatter.DarkMatter
import com.hvs.darkmatter.DarkMatter.Companion.UNIT_SCALE
import com.hvs.darkmatter.ecs.component.FacingComponent
import com.hvs.darkmatter.ecs.component.GraphicComponent
import com.hvs.darkmatter.ecs.component.MoveComponent
import com.hvs.darkmatter.ecs.component.PlayerComponent
import com.hvs.darkmatter.ecs.component.TransformComponent
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.debug
import ktx.log.logger
import kotlin.math.min

class GameScreen(game: DarkMatter) : Screen(game) {

    override fun show() {
        LOG.debug { "Screen is shown" }

        engine.entity {
            with<TransformComponent> { setInitialPosition(4.5f, 8f, 0f) }
            with<GraphicComponent>()
            with<MoveComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }
    }

    override fun render(delta: Float) {
        engine.update(min(MAX_DELTA_TIME, delta))
    }

    companion object {
        val LOG = logger<GameScreen>()

        private const val MAX_DELTA_TIME = 1 / 20f
    }
}
