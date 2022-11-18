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
        state.soulItemDropMultiplier += soulsChange
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

    class MakeItRain(state: GameState) :
        Upgrade(
            state,
            title = "Make It Rain",
            description = "+1 additional projectile & +25% more mob health"
        ) {
        override val soulsChange: Float = 0f
        override fun onCollect() {
            state.extraProjectiles++
        }
    }

    class LargeAndInCharge(state: GameState) :
        Upgrade(
            state,
            title = "Large and In Charge",
            description = "+25% increase projectile radius & +25% more victims speed"
        ) {
        override val soulsChange: Float = 0f
        override fun onCollect() {
            state.projectileDamageRadiusMultiplier += 0.5f
        }
    }

    class ItGoesBoom(state: GameState) :
        Upgrade(
            state,
            title = "It Goes Boom",
            description = "+1 extra explosion of projectile ends & 25% more victims spawn"
        ) {
        override val soulsChange: Float = 0f
        override fun onCollect() {
            state.extraExplosions++
        }
    }

    class SuperChargeMe(state: GameState) :
        Upgrade(
            state,
            title = "Super Charge Me",
            description = "-50% cooldown reduction & -50% health"
        ) {
        override val soulsChange: Float = 0f
        override fun onCollect() {
            state.skillCDMultiplier *= 0.5f
            state.heroHealthMultiplier *= 0.5f
        }
    }

    class ImFragile(state: GameState) :
        Upgrade(
            state,
            title = "I'm Fragile",
            description = "+200% more damage & reset health to 1"
        ) {
        override val soulsChange: Float = 0f
        override fun onCollect() {
            state.heroHealthMultiplier = 0.25f
            state.extraHeroDamage *= 3
        }
    }

    class Juggernaut(state: GameState) :
        Upgrade(
            state,
            title = "Juggernaut",
            description = "+100% more health & damage reduce by 50% and souls by 25%"
        ) {
        override val soulsChange: Float = -0.25f
        override fun onCollect() {
            state.heroHealthMultiplier *= 2f
            state.extraHeroDamage /= 2
        }
    }
}

fun createUpgrades(state: GameState) =
    listOf(
        Upgrade.DamnedVictims(state),
        Upgrade.Quickie(state),
        Upgrade.MakeItRain(state),
        Upgrade.LargeAndInCharge(state),
        Upgrade.ItGoesBoom(state),
        Upgrade.SuperChargeMe(state),
        Upgrade.ImFragile(state),
        Upgrade.Juggernaut(state)
    )