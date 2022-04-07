package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.hvs.darkmatter.ecs.component.PlayerComponent
import com.hvs.darkmatter.ecs.component.RemoveComponent
import com.hvs.darkmatter.ecs.component.TransformComponent
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.max

class DamageSystem: IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformComponent = entity[TransformComponent.componentMapper]
        require(transformComponent != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val playerComponent = entity[PlayerComponent.componentMapper]
        require(playerComponent != null) { "Entity |entity| must have a playerComponent. entity=$entity" }

        if (transformComponent.position.y <= DAMAGE_AREA) {
            var damage = DAMAGE_PER_SECOND * deltaTime
            if (playerComponent.shield > 0f) {
                val blockAmount = playerComponent.shield
                playerComponent.shield = max(0f, playerComponent.shield - damage)
                damage -= blockAmount

                if (damage <= 0f) {
                    //entire damage was blocked
                    return
                }
            }

            playerComponent.life -= damage
            if (playerComponent.life <= 0f) {
                entity.addComponent<RemoveComponent>(engine) {
                    delay = DEATH_EXPLOSION_DURATION
                }
            }
        }
    }

    companion object {
        private const val DAMAGE_AREA = 2f
        private const val DAMAGE_PER_SECOND = 25f
        private const val DEATH_EXPLOSION_DURATION = 0.9f
    }
}
