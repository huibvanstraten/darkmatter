package com.hvs.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import com.hvs.darkmatter.GameEvent
import com.hvs.darkmatter.GameEventListener
import com.hvs.darkmatter.GameEventManager
import ktx.collections.GdxArray

private class CameraShake: Pool.Poolable {
    var maxDistortion = 0f
    var duration = 0f
    lateinit var camera: Camera
    private var storeCameraPosition = true
    private val originalCameraPosition = Vector3()
    private var currentDuration = 0f

    fun update(deltaTime: Float): Boolean {
        if(storeCameraPosition) {
            storeCameraPosition = false
            originalCameraPosition.set(camera.position)
        }

        if (currentDuration < duration) {
            val currentPower = maxDistortion * ((duration - currentDuration) / duration)

            camera.position.x = originalCameraPosition.x + MathUtils.random(-1f, 1f) * currentPower
            camera.position.y = originalCameraPosition.y + MathUtils.random(-1f, 1f) * currentPower
            camera.update()

            currentDuration += deltaTime
            return false
        }

        camera.position.set(originalCameraPosition)
        camera.update()
        return true
    }

    override fun reset() {
        maxDistortion = 0f
        duration = 0f
        currentDuration = 0f
        originalCameraPosition.set(Vector3.Zero)
        storeCameraPosition = true
    }
}

private class CameraShakePool(private val gameCamera: Camera): Pool<CameraShake>() {
    override fun newObject() = CameraShake().apply {
        this.camera = gameCamera
    }
}

class CameraShakeSystem(
    camera: Camera,
    private val gameEventManager: GameEventManager
): EntitySystem(), GameEventListener {

    private val shakePool = CameraShakePool(camera)
    private val activeShakes = GdxArray<CameraShake>()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEvent.PlayerHit::class, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(this)
    }

    override fun update(deltaTime: Float) {
        if (!activeShakes.isEmpty) {
            val shake = activeShakes.first()
            if (shake.update(deltaTime)) {
                activeShakes.removeIndex(0)
                shakePool.free(shake)
            }
        }
    }

    override fun onEvent(event: GameEvent) {
        if (activeShakes.size < 4) {
            activeShakes.add(shakePool.obtain().apply {
                duration = 0.25f
                maxDistortion = 0.25f
            })
        }
    }
}
