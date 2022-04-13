package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.hvs.darkmatter.ecs.component.AttachComponent
import com.hvs.darkmatter.ecs.component.GraphicComponent
import com.hvs.darkmatter.ecs.component.PlayerComponent
import com.hvs.darkmatter.ecs.component.RemoveComponent
import com.hvs.darkmatter.ecs.component.TransformComponent
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get

class AttachSystem: EntityListener,
    IteratingSystem(allOf(AttachComponent::class, TransformComponent::class, GraphicComponent::class).get()) {

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) = Unit

    override fun entityRemoved(removedEntity: Entity) {
        entities.forEach { entity ->
            entity[AttachComponent.componentMapper]?.let { attachComponent ->
                if (attachComponent.entity == removedEntity) {
                    entity.addComponent<RemoveComponent>(engine)
                }
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val graphicComponent = entity[GraphicComponent.componentMapper]
        require(graphicComponent != null) { "Entity |entity| must have a graphicComponent. entity=$entity" }
        val transformComponent = entity[TransformComponent.componentMapper]
        require(transformComponent != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val attachComponent = entity[AttachComponent.componentMapper]
        require(attachComponent != null) { "Entity |entity| must have a AttachComponent. entity=$entity" }

        //update position
            attachComponent.entity[TransformComponent.componentMapper]?.let { attachTransform ->
                transformComponent.interpolatedPosition.set(
                    attachTransform.interpolatedPosition.x + attachComponent.offset.x,
                    attachTransform.interpolatedPosition.y + attachComponent.offset.y,
                    transformComponent.position.z
                )
            }

        //update graphic alpha value
        attachComponent.entity[GraphicComponent.componentMapper]?.let {  attachGraphic ->
            graphicComponent.sprite.setAlpha(attachGraphic.sprite.color.a)
        }
    }

}
