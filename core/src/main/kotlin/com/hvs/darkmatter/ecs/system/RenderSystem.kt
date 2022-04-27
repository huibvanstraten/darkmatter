package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.hvs.darkmatter.GameEvent
import com.hvs.darkmatter.GameEventListener
import com.hvs.darkmatter.GameEventManager
import com.hvs.darkmatter.ecs.component.GraphicComponent
import com.hvs.darkmatter.ecs.component.PlayerComponent
import com.hvs.darkmatter.ecs.component.PowerUpType
import com.hvs.darkmatter.ecs.component.RemoveComponent
import com.hvs.darkmatter.ecs.component.TransformComponent
import com.hvs.darkmatter.screen.GameScreen.Companion.LOG
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.error
import kotlin.math.min

class RenderSystem(
    private val batch: Batch,
    private val gameViewPort: Viewport,
    private val uiViewport: Viewport,
    backgroundTexture: Texture,
    private val gameEventManager: GameEventManager,
    private val outlineShader: ShaderProgram
): GameEventListener, SortedIteratingSystem(
    allOf(TransformComponent::class, GraphicComponent::class).get(),
    compareBy {entity -> entity[TransformComponent.componentMapper] }
) {
    private val background = Sprite(backgroundTexture.apply {
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    })
    private val backgroundScrollSpeed = Vector2(0.03f, -0.25f)

    private val textureSizeLocation = outlineShader.getUniformLocation("u_textureSize")
    private val outlineColorLocation = outlineShader.getUniformLocation("u_outlineColor")
    private val outlineColor = Color(0f, 113f/225f, 214f/255f, 1f)
    private val playerEntities by lazy {
        engine.getEntitiesFor(allOf(PlayerComponent::class).exclude(RemoveComponent::class).get())
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEvent.CollectPowerUp::class, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEvent.CollectPowerUp::class, this)
    }

    override fun update(deltaTime: Float) {
        forceSort()
        uiViewport.apply()
        batch.use(uiViewport.camera.combined) {
            //render background
            background.run {
                backgroundScrollSpeed.y = min(-0.25f,
                backgroundScrollSpeed.y + deltaTime * (1f/10f))
                scroll(backgroundScrollSpeed.x * deltaTime, backgroundScrollSpeed.y * deltaTime)
                draw(batch)
            }
        }
        gameViewPort.apply()
        batch.use(gameViewPort.camera.combined) {
            //render entity
            super.update(deltaTime)
        }

        //render outlines of entities
        renderEntityOutlines()
    }

    private fun renderEntityOutlines() {
        batch.use(gameViewPort.camera.combined) {
            it.shader = outlineShader
            playerEntities.forEach { entity ->
                renderPlayerOutlines(entity, it)
            }
            it.shader = null
        }
    }

    private fun renderPlayerOutlines(entity: Entity, it: Batch) {
        val playerComponent = entity[PlayerComponent.componentMapper]
        require(playerComponent != null) { "Entity |entity| must have a PlayerComponent. entity=$entity" }

        if (playerComponent.shield > 0) {
            outlineColor.a = MathUtils.clamp(playerComponent.shield/playerComponent.maxShield, 0f, 1f)
            outlineShader.setUniformf(outlineColorLocation, outlineColor)
            entity[GraphicComponent.componentMapper]?.let { graphic ->
                graphic.sprite.run {
                    outlineShader.setUniformf(textureSizeLocation, texture.width.toFloat(), texture.height.toFloat())
                    draw(batch)
                }
            }
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

    override fun onEvent(event: GameEvent) {
        val powerUpEvent = event as GameEvent.CollectPowerUp
        if (powerUpEvent.type == PowerUpType.SPEED_1) {
            backgroundScrollSpeed.y -= 0.25f
        } else if (powerUpEvent.type == PowerUpType.SPEED_2) {
            backgroundScrollSpeed.y -= 0.5f
        }
    }
}

