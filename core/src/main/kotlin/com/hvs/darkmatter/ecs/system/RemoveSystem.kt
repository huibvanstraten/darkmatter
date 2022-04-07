package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.hvs.darkmatter.ecs.component.GraphicComponent
import com.hvs.darkmatter.ecs.component.RemoveComponent
import ktx.ashley.allOf
import ktx.ashley.get

class RemoveSystem: IteratingSystem(allOf(RemoveComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val removeComponent = entity[RemoveComponent.componentMapper]
        require(removeComponent != null) { "Entity |entity| must have a removeComponent. entity=$entity" }

        removeComponent.delay -= deltaTime
        if (removeComponent.delay < 0f) {
            engine.removeEntity(entity)
        }
    }
}
