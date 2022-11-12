package com.lehaine.game

import kotlin.math.max
import kotlin.math.min

/**
 * @author Colton Daily
 * @date 4/1/2022
 */
object Config {
    const val VIRTUAL_WIDTH = 480
    const val VIRTUAL_HEIGHT = 270
    const val GRID_CELL_SIZE = 16

    var cameraShakeMultiplier = 1f
        set(value) {
            field = max(0f, min(value, 1f))
        }

    var keyboardType = KeyboardType.QWERTY

    enum class KeyboardType {
        QWERTY,
        AZERTY
    }
}