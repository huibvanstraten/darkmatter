package com.hvs.darkmatter.screen

import com.badlogic.gdx.graphics.g2d.Batch
import com.hvs.darkmatter.DarkMatter
import ktx.app.KtxScreen

abstract class Screen(
    game: DarkMatter,
    batch: Batch = game.batch
): KtxScreen