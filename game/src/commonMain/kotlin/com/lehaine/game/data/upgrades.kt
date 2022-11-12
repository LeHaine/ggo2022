/**
 * @author Colton Daily
 * @date 11/12/2022
 */
package com.lehaine.game.data

import com.lehaine.game.GameState


sealed class Upgrade(
    protected val state: GameState,
    val title: String,
    val description: String
) {
    open val soulsChange: Float = 0f

    fun collect() {
        state.soulCollectibleDropMultiplier += soulsChange
        onCollect()
    }

    protected open fun onCollect() = Unit

    class DamnedVictims(state: GameState) :
        Upgrade(
            state,
            title = "Damned Victims",
            description = "+10% souls drop & +10% damned victims spawn."
        ) {
        override val soulsChange: Float = 0.1f
        override fun onCollect() {
            state.totalMonstersSpawnMultiplier += 0.1f
        }
    }

    class Quickie(state: GameState) :
        Upgrade(
            state,
            title = "A Quickie",
            description = "+15% souls drop & +15% faster spawn of a damned victim."
        ) {
        override val soulsChange: Float = 0.15f
        override fun onCollect() {
            state.monsterRespawnMultiplier += 0.15f
        }
    }
}

fun createUpgrades(state: GameState) = listOf(Upgrade.DamnedVictims(state), Upgrade.Quickie(state))