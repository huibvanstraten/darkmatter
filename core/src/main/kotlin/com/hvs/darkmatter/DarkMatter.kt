package com.hvs.darkmatter

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.hvs.darkmatter.audio.AudioService
import com.hvs.darkmatter.audio.DefaultAudioService
import com.hvs.darkmatter.ecs.asset.BitmapFontAsset
import com.hvs.darkmatter.ecs.asset.ShaderProgramAsset
import com.hvs.darkmatter.ecs.asset.TextureAsset
import com.hvs.darkmatter.ecs.asset.TextureAtlasAsset
import com.hvs.darkmatter.ecs.system.AnimationSystem
import com.hvs.darkmatter.ecs.system.AttachSystem
import com.hvs.darkmatter.ecs.system.CameraShakeSystem
import com.hvs.darkmatter.ecs.system.DamageSystem
import com.hvs.darkmatter.ecs.system.DebugSystem
import com.hvs.darkmatter.ecs.system.MoveSystem
import com.hvs.darkmatter.ecs.system.PlayerAnimationSystem
import com.hvs.darkmatter.ecs.system.PlayerInputSystem
import com.hvs.darkmatter.ecs.system.PowerUpSystem
import com.hvs.darkmatter.ecs.system.RemoveSystem
import com.hvs.darkmatter.ecs.system.RenderSystem
import com.hvs.darkmatter.screen.LoadingScreen
import com.hvs.darkmatter.screen.Screen
import com.hvs.darkmatter.ui.createSkin
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.app.KtxGame
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.preferences.set

class DarkMatter : KtxGame<Screen>() {
    val uiViewport = FitViewport(VIRTUAL_WIDTH_PIXELS.toFloat(), VIRTUAL_HEIGHT_PIXELS.toFloat())
    val stage: Stage by lazy {
        val result = Stage(uiViewport, batch)
        Gdx.input.inputProcessor = result
        result
    }

    val gameViewPort = FitViewport(VIRTUAL_WIDTH.toFloat(), VIRTUAL_HEIGHT.toFloat())
    private val batch: Batch by lazy { SpriteBatch() }
    val gameEventManager = GameEventManager()
    val assets: AssetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage()
    }
    val audioService: AudioService by lazy { DefaultAudioService(assets) }
    val preferences: Preferences by lazy { Gdx.app.getPreferences("darkMatter") }

    val engine: Engine by lazy {
        preferences["key"] = 3.5f

        PooledEngine().apply {
            val graphicsAtlas = assets[TextureAtlasAsset.GAME_GRAPHICS.descriptor]

            addSystem(PlayerInputSystem(gameViewPort))
            addSystem(MoveSystem())
            addSystem(PowerUpSystem(gameEventManager, audioService))
            addSystem(DamageSystem(gameEventManager))
            addSystem(CameraShakeSystem(gameViewPort.camera, gameEventManager))
            addSystem(
                PlayerAnimationSystem(
                    graphicsAtlas.findRegion("ship_base"),
                    graphicsAtlas.findRegion("ship_left"),
                    graphicsAtlas.findRegion("ship_right")
                )
            )
            addSystem(AttachSystem())
            addSystem(AnimationSystem(graphicsAtlas))
            addSystem(
                RenderSystem(
                    batch, gameViewPort,
                    uiViewport,
                    assets[TextureAsset.BACKGROUND.descriptor],
                    gameEventManager,
                    assets[ShaderProgramAsset.OUTLINE.descriptor]
                )
            )
            addSystem(RemoveSystem())
            addSystem(DebugSystem())
        }
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG

        val assetReferences = gdxArrayOf(
            TextureAtlasAsset.values().filter { it.isSkinAtlas }.map { assets.loadAsync(it.descriptor) },
            BitmapFontAsset.values().map { assets.loadAsync(it.descriptor) }
        ).flatten()
        KtxAsync.launch {
            assetReferences.joinAll()
            createSkin(assets)

            addScreen(LoadingScreen(this@DarkMatter))
            setScreen<LoadingScreen>()
        }
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        assets.dispose()
        stage.dispose()
    }

    companion object {
        const val UNIT_SCALE = 1 / 16f
        const val VIRTUAL_WIDTH = 9
        const val VIRTUAL_HEIGHT = 16
        const val VIRTUAL_WIDTH_PIXELS = 135
        const val VIRTUAL_HEIGHT_PIXELS = 240
    }
}
