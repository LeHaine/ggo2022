package com.lehaine.game

import com.lehaine.littlekt.createLittleKtApp
import com.lehaine.littlekt.graphics.Color
import com.lehaine.littlekt.graphics.HdpiMode

fun main() {
    createLittleKtApp {
        width = 1000
        height = 900
        backgroundColor = Color.DARK_GRAY
        resizeable = false
        title = "Glutton for Punishment"
        val isMac = System.getProperty("os.name").lowercase().contains("mac")
        if (isMac) {
            hdpiMode = HdpiMode.PIXELS
        }
    }.start {
        GameCore(it)
    }
}