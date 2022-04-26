package com.hvs.darkmatter.screen

import com.badlogic.ashley.core.Engine
import com.hvs.darkmatter.DarkMatter
import com.hvs.darkmatter.DarkMatter.Companion.UNIT_SCALE
import com.hvs.darkmatter.DarkMatter.Companion.VIRTUAL_WIDTH
import com.hvs.darkmatter.GameEvent
import com.hvs.darkmatter.GameEventListener
import com.hvs.darkmatter.ecs.asset.MusicAsset
import com.hvs.darkmatter.ecs.component.AnimationComponent
import com.hvs.darkmatter.ecs.component.AnimationType
import com.hvs.darkmatter.ecs.component.AttachComponent
import com.hvs.darkmatter.ecs.component.FacingComponent
import com.hvs.darkmatter.ecs.component.GraphicComponent
import com.hvs.darkmatter.ecs.component.MoveComponent
import com.hvs.darkmatter.ecs.component.PlayerComponent
import com.hvs.darkmatter.ecs.component.TransformComponent
import com.hvs.darkmatter.ecs.system.DamageSystem.Companion.DAMAGE_AREA
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.debug
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import kotlin.math.min

class GameScreen(
    game: DarkMatter,
    val engine: Engine = game.engine
) : Screen(game), GameEventListener {

    override fun show() {
        LOG.debug { "Screen is shown" }
        val highscore: Float = preferences["highscore", 0f]
        LOG.debug { "$highscore" }
        gameEventManager.addListener(GameEvent.PlayerDeath::class, this)

        audioService.play(MusicAsset.GAME)
        spawnPlayer()

        engine.entity {
            with<TransformComponent> {
                size.set(
                    VIRTUAL_WIDTH.toFloat(),
                    DAMAGE_AREA
                )
            }
            with<AnimationComponent> { type = AnimationType.DARK_MATTER }
            with<GraphicComponent>()
        }
    }

    override fun hide() {
        super.hide()
        gameEventManager.removeListener(this)
    }

    override fun render(delta: Float) {
        engine.update(min(MAX_DELTA_TIME, delta))
        audioService.update()
    }

    override fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.PlayerDeath -> {
                LOG.debug { "player died with a distance of ${event.distance}" }
                preferences.flush {
                    this["highscore"] = event.distance
                }
                spawnPlayer()
            }
            GameEvent.CollectPowerUp -> TODO()
        }
    }

    private fun spawnPlayer() {
        val playerShip = engine.entity {
            with<TransformComponent> { setInitialPosition(4.5f, 8f, -1f) }
            with<GraphicComponent>()
            with<MoveComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }

        engine.entity {
            with<TransformComponent>()
            with<AttachComponent> {
                entity = playerShip
                offset.set(1f * UNIT_SCALE, -6f * UNIT_SCALE)
            }
            with<GraphicComponent>()
            with<AnimationComponent> { type = AnimationType.FIRE }
        }
    }

    companion object {
        val LOG = logger<GameScreen>()

        private const val MAX_DELTA_TIME = 1 / 20f
    }
}
