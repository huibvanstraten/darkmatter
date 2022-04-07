package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.hvs.darkmatter.ecs.component.PlayerComponent
import com.hvs.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.getSystem
import kotlin.math.max
import kotlin.math.min

class DebugSystem: IntervalIteratingSystem(allOf(PlayerComponent::class).get(), WINDOW_UPDATE_RATE) {
    init {
        setProcessing(true)
    }

    override fun processEntity(entity: Entity) {
        val transformComponent = entity[TransformComponent.componentMapper]
        require(transformComponent != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val playerComponent = entity[PlayerComponent.componentMapper]
        require(playerComponent != null) { "Entity |entity| must have a playerComponent. entity=$entity" }

        when{
            Gdx.input.isKeyPressed(Input.Keys.NUM_1) -> {
                //kill player
                transformComponent.position.y = 1f
                playerComponent.life = 1f
                playerComponent.shield = 0f

            }
            Gdx.input.isKeyPressed(Input.Keys.NUM_2) -> {
                //add shield
                playerComponent.shield = min(playerComponent.maxShield, playerComponent.shield + 25f)
            }
            Gdx.input.isKeyPressed(Input.Keys.NUM_3) -> {
                //remove shield
                playerComponent.shield = max(playerComponent.maxShield, playerComponent.shield - 25f)
            }
            Gdx.input.isKeyPressed(Input.Keys.NUM_4) -> {
                //disable movement
                engine.getSystem<MoveSystem>().setProcessing(false)
            }
            Gdx.input.isKeyPressed(Input.Keys.NUM_5) -> {
                //enable movement
                engine.getSystem<MoveSystem>().setProcessing(true)
            }
        }
        Gdx.graphics.setTitle("DM DEBUG position: " +
                "${transformComponent.position}, " +
                "player life:${playerComponent.life}, " +
                "player shield:${playerComponent.shield}"
        )
    }

    companion object {
        private const val WINDOW_UPDATE_RATE = 0.25f
    }



}
