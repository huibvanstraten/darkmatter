package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.hvs.darkmatter.DarkMatter
import com.hvs.darkmatter.DarkMatter.Companion.VIRTUAL_HEIGHT
import com.hvs.darkmatter.DarkMatter.Companion.VIRTUAL_WIDTH
import com.hvs.darkmatter.ecs.component.FacingComponent
import com.hvs.darkmatter.ecs.component.GraphicComponent
import com.hvs.darkmatter.ecs.component.MoveComponent
import com.hvs.darkmatter.ecs.component.PlayerComponent
import com.hvs.darkmatter.ecs.component.RemoveComponent
import com.hvs.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.max
import kotlin.math.min

class MoveSystem :
    IteratingSystem(allOf(TransformComponent::class, MoveComponent::class).exclude(RemoveComponent::class).get()) {
    private var accumulator = 0f

    override fun update(deltaTime: Float) {
        accumulator += deltaTime
        while (accumulator > UPDATE_RATE) {
            accumulator -= UPDATE_RATE

            entities.forEach { entity ->
                entity[TransformComponent.componentMapper]?.let { transform ->
                    transform.previousPosition.set(transform.position)
                }
            }

            super.update(UPDATE_RATE)
        }

        val alpha = accumulator / UPDATE_RATE
        entities.forEach { entity ->
            entity[TransformComponent.componentMapper]?.let { transform ->
                transform.interpolatedPosition.set(
                    MathUtils.lerp(transform.previousPosition.x, transform.position.x, alpha),
                    MathUtils.lerp(transform.previousPosition.y, transform.position.y, alpha),
                    transform.position.z
                )
            }
        }

    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformComponent = entity[TransformComponent.componentMapper]
        require(transformComponent != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val moveComponent = entity[MoveComponent.componentMapper]
        require(moveComponent != null) { "Entity |entity| must have a MoveComponent. entity=$entity" }

        val player = entity[PlayerComponent.componentMapper]
        if (player != null) {
            entity[FacingComponent.componentMapper]?.let {
                movePlayer(transformComponent, moveComponent, player, it, deltaTime)
            }
        } else {
            moveEntity(transformComponent, moveComponent, deltaTime)
        }
    }

    private fun moveEntity(transformComponent: TransformComponent, moveComponent: MoveComponent, deltaTime: Float) {
        transformComponent.position.x = MathUtils.clamp(
            transformComponent.position.x + moveComponent.speed.x * deltaTime,
            0f,
            VIRTUAL_WIDTH - transformComponent.size.x
        )
        transformComponent.position.y = MathUtils.clamp(
            transformComponent.position.y + moveComponent.speed.y * deltaTime,
            1f,
            VIRTUAL_HEIGHT + 1f - transformComponent.size.y
        )
    }

    private fun movePlayer(
        transformComponent: TransformComponent,
        moveComponent: MoveComponent,
        player: PlayerComponent,
        facingComponent: FacingComponent,
        deltaTime: Float
    ) {
        moveComponent.speed.x = when (facingComponent.facingDirection) {
            FacingComponent.FacingDirection.LEFT -> min(0f, moveComponent.speed.x - HOR_ACCELERATION * deltaTime)
            FacingComponent.FacingDirection.RIGHT -> max(0f, moveComponent.speed.x + HOR_ACCELERATION * deltaTime)
            else -> 0f
        }

        moveComponent.speed.x = MathUtils.clamp(moveComponent.speed.x,
            -MAX_HOR_SPEED,
            MAX_HOR_SPEED
        )
        moveComponent.speed.y = MathUtils.clamp(
            moveComponent.speed.y - VERT_ACCELERATION * deltaTime,
            -MAX_VERT_NEG_PLAYER_SPEED,
            MAX_VERT_POS_PLAYER_SPEED
        )

        moveEntity(transformComponent, moveComponent, deltaTime)
    }

    companion object {
        const val UPDATE_RATE = 1 / 25f
        const val HOR_ACCELERATION = 16.5f
        const val VERT_ACCELERATION = 2.25f
        const val MAX_VERT_NEG_PLAYER_SPEED = 0.75f
        const val MAX_VERT_POS_PLAYER_SPEED = 5f
        const val MAX_HOR_SPEED = 5.5f
    }
}
