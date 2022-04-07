package com.hvs.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class TransformComponent: Component, Pool.Poolable, Comparable<TransformComponent> {
    val position = Vector3()
    val previousPosition = Vector3()
    val interpolatedPosition =  Vector3()
    val size = Vector2(1f, 1f)
    var rotationDegrees = 0f

    override fun reset() {
        position.set(Vector3.Zero)
        previousPosition.set(Vector3.Zero)
        interpolatedPosition.set(Vector3.Zero)
        size.set(1f, 1f)
        rotationDegrees = 0f
    }

    fun setInitialPosition(x: Float, y: Float, z: Float) {
        position.set(x, y, z)
        previousPosition.set(x, y, z)
        interpolatedPosition.set(x, y, z)
    }

    override fun compareTo(other: TransformComponent): Int {
        val zDiff = position.z - other.position.z
        return (if(zDiff == 0f) position.y - other.position.y else zDiff).toInt()
    }

    companion object {
        val componentMapper = mapperFor<TransformComponent>()
    }
}
