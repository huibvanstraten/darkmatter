package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import com.hvs.darkmatter.DarkMatter
import com.hvs.darkmatter.ecs.component.GraphicComponent
import com.hvs.darkmatter.ecs.component.TransformComponent
import com.hvs.darkmatter.screen.GameScreen.Companion.LOG
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.error

class RenderSystem(
    private val batch: Batch,
    private val gameViewPort: Viewport
): SortedIteratingSystem(
    allOf(TransformComponent::class, GraphicComponent::class).get(),
    compareBy {entity -> entity[TransformComponent.componentMapper] }
) {

    override fun update(deltaTime: Float) {
        forceSort()
        gameViewPort.apply()
        batch.use(gameViewPort.camera.combined) {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformComponent = entity[TransformComponent.componentMapper]
        require(transformComponent != null) {"Entity |entity| must have a TransformComponent. entity=$entity"}
        val graphicComponent = entity[GraphicComponent.componentMapper]
        require(graphicComponent != null) {"Entity |entity| must have a GraphicComponent. entity=$entity"}

        if (graphicComponent.sprite.texture == null) {
            LOG.error { "Entity has no texture for rendering. entity=$entity" }
        }

        graphicComponent.sprite.run {
            rotation = transformComponent.rotationDegrees

            setBounds(
                transformComponent.position.x,
                transformComponent.position.y,
                transformComponent.size.x,
                transformComponent.size.y
            )

            draw(batch)
        }
    }
}
