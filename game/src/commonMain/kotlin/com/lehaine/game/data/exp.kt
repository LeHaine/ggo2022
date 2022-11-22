package com.lehaine.game.data

import com.lehaine.littlekt.math.floorToInt
import com.lehaine.littlekt.util.signal2v
import kotlin.math.pow

/**
 * @author Colton Daily
 * @date 11/13/2022
 */
class ExpTable {
    val onLevelUp = signal2v<Int, Int>()
    var exp = 0
        private set
    var level = 1
        private set

    val expRemaining: Int get() = expToNextLevel - exp

    val expToNextLevel: Int
        get() = (25f * (1 + 0.25f).pow(level)).floorToInt()

    val ratioToNextLevel: Float get() = exp / expToNextLevel.toFloat()

    fun add(expToAdd: Int) {
        val levelsGained = add(expToAdd, 0)
        if (levelsGained > 0) {
            onLevelUp.emit(level, levelsGained)
        }
    }

    private fun add(expToAdd: Int, totalLevelsGained: Int): Int {
        val remaining = expToAdd - expRemaining
        exp += if (remaining > 0) {
            expToAdd - remaining
        } else {
            expToAdd
        }

        var levelsGained = 0
        if (exp >= expToNextLevel) {
            level++
            levelsGained = 1
            exp = 0
        }

        if (remaining > 0) {
            levelsGained = add(remaining, totalLevelsGained + levelsGained)
        }
        return if (remaining > 0) levelsGained else totalLevelsGained + levelsGained
    }
}