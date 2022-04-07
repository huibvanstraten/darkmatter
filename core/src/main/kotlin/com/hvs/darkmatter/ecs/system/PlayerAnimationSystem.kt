package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hvs.darkmatter.ecs.component.FacingComponent
import com.hvs.darkmatter.ecs.component.GraphicComponent
import com.hvs.darkmatter.ecs.component.PlayerComponent
import com.hvs.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerAnimationSystem(
    private val defaultRegion: TextureRegion,
    private val leftRegion: TextureRegion,
    private val rightRegion: TextureRegion
) : IteratingSystem(allOf(FacingComponent::class, PlayerComponent::class, TransformComponent::class).get()),
    EntityListener {

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) {
        entity[GraphicComponent.componentMapper]?.setSpriteRegion(defaultRegion)
    }

    override fun entityRemoved(entity: Entity) = Unit

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val graphicComponent = entity[GraphicComponent.componentMapper]
        require(graphicComponent != null) { "Entity |entity| must have a graphicComponent. entity=$entity" }
        val facingComponent = entity[FacingComponent.componentMapper]
        require(facingComponent != null) { "Entity |entity| must have a facingComponent. entity=$entity" }

        if (facingComponent.facingDirection == facingComponent.lastDirection && graphicComponent.sprite.texture != null) {
            return
        }

        facingComponent.lastDirection = facingComponent.facingDirection
        val region = when (facingComponent.facingDirection) {
            FacingComponent.FacingDirection.LEFT -> leftRegion
            FacingComponent.FacingDirection.RIGHT -> rightRegion
            else -> defaultRegion
        }

        graphicComponent.setSpriteRegion(region)
    }
}
