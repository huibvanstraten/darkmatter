package com.hvs.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Pool
import com.hvs.darkmatter.screen.Screen
import ktx.ashley.get
import ktx.ashley.mapperFor
import kotlin.reflect.KClass

class SetScreenComponent : Component, Pool.Poolable {
    var screenType: KClass<out Screen> = Screen::class
    var screenData: Any? = null

    override fun reset() {
        screenType = Screen::class
        screenData = null
    }

    companion object {
        val MAPPER = mapperFor<SetScreenComponent>()
    }
}

val Entity.setScreenCmp: SetScreenComponent
    get() = this[SetScreenComponent.MAPPER]
        ?: throw GdxRuntimeException("SetScreenComponent for entity '$this' is null")
