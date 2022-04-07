package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.hvs.darkmatter.ecs.component.FacingComponent
import com.hvs.darkmatter.ecs.component.PlayerComponent
import com.hvs.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerInputSystem(
    private val gameViewport: Viewport
): IteratingSystem(allOf(
        PlayerComponent::class,
        TransformComponent::class,
        FacingComponent::class
    ).get())
{
    private val tempVector = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformComponent = entity[TransformComponent.componentMapper]
        require(transformComponent != null) {"Entity |entity| must have a TransformComponent. entity=$entity"}
        val facingComponent = entity[FacingComponent.componentMapper]
        require(facingComponent != null) {"Entity |entity| must have a facingComponent. entity=$entity"}

        tempVector.x = Gdx.input.x.toFloat()
        gameViewport.unproject(tempVector)

        val diffX = tempVector.x - transformComponent.position.x - transformComponent.size.x * 0.5f

        facingComponent.facingDirection = when {
            diffX < 0 -> FacingComponent.FacingDirection.LEFT
            diffX > 0 -> FacingComponent.FacingDirection.RIGHT
            else -> FacingComponent.FacingDirection.DEFAULT
        }



    }

}
