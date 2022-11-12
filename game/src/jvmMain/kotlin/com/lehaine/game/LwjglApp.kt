package com.lehaine.game

import com.lehaine.littlekt.createLittleKtApp
import com.lehaine.littlekt.graphics.Color

fun main() {
    createLittleKtApp {
        width = 1000
        height = 900
        backgroundColor = Color.DARK_GRAY
        resizeable = false
        title = "Glutton for Punishment"
    }.start {
        GameCore(it)
    }
}