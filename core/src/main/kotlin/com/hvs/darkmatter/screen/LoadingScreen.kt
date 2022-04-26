package com.hvs.darkmatter.screen

import com.hvs.darkmatter.DarkMatter
import com.hvs.darkmatter.ecs.asset.ShaderProgramAsset
import com.hvs.darkmatter.ecs.asset.SoundAsset
import com.hvs.darkmatter.ecs.asset.TextureAsset
import com.hvs.darkmatter.ecs.asset.TextureAtlasAsset
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf

class LoadingScreen(game: DarkMatter) : Screen(game) {

    override fun show() {
        val assetRefs = gdxArrayOf(
            //queue asset loading
            TextureAsset.values().map { assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.values().map { assets.loadAsync(it.descriptor) },
            SoundAsset.values().map { assets.loadAsync(it.descriptor) },
            ShaderProgramAsset.values().map { assets.loadAsync(it.descriptor) }
        ).flatten()

        //once assets are loaded -> change to GameScreen
        KtxAsync.launch {
            assetRefs.joinAll()
            assetsLoaded()
        }

        //setup UI
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game))
        game.setScreen<GameScreen>()
        game.removeScreen<LoadingScreen>()
        dispose()
    }
}
