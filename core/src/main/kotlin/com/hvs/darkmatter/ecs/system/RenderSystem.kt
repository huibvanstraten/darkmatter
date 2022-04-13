package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.hvs.darkmatter.ecs.component.GraphicComponent
import com.hvs.darkmatter.ecs.component.TransformComponent
import com.hvs.darkmatter.screen.GameScreen.Companion.LOG
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.error

class RenderSystem(
    private val batch: Batch,
    private val gameViewPort: Viewport,
    private val uiViewport: Viewport,
    backgroundTexture: Texture
): SortedIteratingSystem(
    allOf(TransformComponent::class, GraphicComponent::class).get(),
    compareBy {entity -> entity[TransformComponent.componentMapper] }
) {
    private val background = Sprite(backgroundTexture.apply {
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    })
    private val backgroundScrollSpeed = Vector2(0.03f, -0.25f)

    override fun update(deltaTime: Float) {
        forceSort()
        uiViewport.apply()
        batch.use(uiViewport.camera.combined) {
            //render background
            background.run {
                scroll(backgroundScrollSpeed.x * deltaTime, backgroundScrollSpeed.y * deltaTime)
                draw(batch)
            }
        }
        gameViewPort.apply()
        batch.use(gameViewPort.camera.combined) {
            //render entity
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
            return
        }

        graphicComponent.sprite.run {
            rotation = transformComponent.rotationDegrees

            setBounds(
                transformComponent.interpolatedPosition.x,
                transformComponent.interpolatedPosition.y,
                transformComponent.size.x,
                transformComponent.size.y
            )

            draw(batch)
        }
    }
}
