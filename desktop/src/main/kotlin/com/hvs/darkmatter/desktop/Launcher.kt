package com.hvs.darkmatter.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.hvs.darkmatter.DarkMatter

fun main() {
    Lwjgl3Application(
        DarkMatter(),
        Lwjgl3ApplicationConfiguration().apply {
            setTitle("Dark Matter")
            setWindowSizeLimits(360, 640, -1, -1)
            setWindowedMode(9 * 32, 16 * 32)
            setWindowIcon("icon.png")
        })
}
