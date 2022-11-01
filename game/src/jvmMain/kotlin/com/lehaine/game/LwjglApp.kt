package com.lehaine.game

import com.lehaine.littlekt.createLittleKtApp
import com.lehaine.littlekt.graphics.Color

fun main() {
    createLittleKtApp {
        width = 960
        height = 540
        backgroundColor = Color.DARK_GRAY
        title = "LittleKt Game Template"
    }.start {
        GameCore(it)
    }
}