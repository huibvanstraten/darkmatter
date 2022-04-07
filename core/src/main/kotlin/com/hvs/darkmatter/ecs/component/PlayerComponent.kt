package com.hvs.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class PlayerComponent: Component, Pool.Poolable {
    var life = MAX_LIFE
    var maxLife = MAX_LIFE
    var shield = 0f
    var maxShield = MAX_SHIELD
    var distanceTraveled = 0f

    override fun reset() {
        var life = MAX_LIFE
        var maxLife = MAX_LIFE
        var shield = 0f
        var maxShield = MAX_SHIELD
        var distanceTraveled = 0f
    }

    companion object {
        const val MAX_LIFE = 100f
        const val MAX_SHIELD = 100f

        val componentMapper = mapperFor<PlayerComponent>()
    }

}
