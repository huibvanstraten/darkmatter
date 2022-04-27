package com.hvs.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Pool
import ktx.ashley.get
import ktx.ashley.mapperFor

class MoveComponent: Component, Pool.Poolable{
    val speed = Vector2()
    var cosDeg = 0f
    var sinDeg = 0f
    var root = false




    override fun reset() {
        speed.set(0f, 0f)
        var cosDeg = 0f
        var sinDeg = 0f
        var root = false

    }

    companion object {
        val componentMapper = mapperFor<MoveComponent>()
    }
}

val Entity.moveCmp: MoveComponent
    get() = this[MoveComponent.componentMapper]
        ?: throw GdxRuntimeException("MoveComponent for entity '$this' is null")
