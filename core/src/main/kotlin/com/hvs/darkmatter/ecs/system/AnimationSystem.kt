package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.GdxRuntimeException
import com.hvs.darkmatter.ecs.component.Animation2D
import com.hvs.darkmatter.ecs.component.AnimationComponent
import com.hvs.darkmatter.ecs.component.AnimationType
import com.hvs.darkmatter.ecs.component.GraphicComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import java.util.EnumMap

class AnimationSystem(
    private val atlas: TextureAtlas
) : IteratingSystem(allOf(AnimationComponent::class, GraphicComponent::class).get()), EntityListener {
    private val animationCache = EnumMap<AnimationType, Animation2D>(AnimationType::class.java)


    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animationComponent = entity[AnimationComponent.componentMapper]
        require(animationComponent != null) { "Entity | entity| must have an AnimationComponent. entity=$entity" }
        val graphicComponent = entity[GraphicComponent.componentMapper]
        require(graphicComponent != null) { "Entity | entity| must have a GraphicComponent. entity=$entity" }

        if (animationComponent.type == AnimationType.NONE) {
            LOG.error { "no type specified for animation component $animationComponent for |entity| $entity" }
            return
        }

        if (animationComponent.animation.type == animationComponent.type) {
            //animation is correct -> update it
            animationComponent.stateTime += deltaTime
        } else {
            animationComponent.stateTime = 0f
            animationComponent.animation = getAnimation(animationComponent.type)
        }

        val frame = animationComponent.animation.getKeyFrame(animationComponent.stateTime)
        graphicComponent.setSpriteRegion(frame)
    }

    override fun entityAdded(entity: Entity) {
        entity[AnimationComponent.componentMapper]?.let { animationComp ->
            animationComp.animation = getAnimation(animationComp.type)
            val frame = animationComp.animation.getKeyFrame(animationComp.stateTime)
            entity[GraphicComponent.componentMapper]?.setSpriteRegion(frame)
        }
    }

    private fun getAnimation(type: AnimationType): Animation2D {
        var animation = animationCache[type]
        if (animation == null) {
            var regions = atlas.findRegions(type.atlasKey)
            if (regions.isEmpty) {
                LOG.error { "No regions found for ${type.atlasKey}" }
                regions = atlas.findRegions("error")
                if (regions.isEmpty) throw GdxRuntimeException("There is no error region in the atlas")
            } else {
                LOG.debug { "Adding animation of type $type with ${regions.size} regions" }
            }
            animation = Animation2D(type, regions, type.playMode, type.speedRate)
            animationCache[type] = animation
        }
        return animation
    }

    override fun entityRemoved(entity: Entity) = Unit

    companion object {
        private val LOG = logger<AnimationSystem>()
    }

}
