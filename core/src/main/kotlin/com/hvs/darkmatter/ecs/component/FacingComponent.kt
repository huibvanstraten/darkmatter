package com.hvs.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class FacingComponent: Component, Pool.Poolable {
    var facingDirection = FacingDirection.DEFAULT
    var lastDirection = FacingDirection.DEFAULT

    override fun reset() {
        var facingDirection = FacingDirection.DEFAULT
    }

    companion object {
        val componentMapper = mapperFor<FacingComponent>()
    }

    enum class FacingDirection {
        DEFAULT, RIGHT, LEFT
    }

}
