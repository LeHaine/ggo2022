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

    fun collect() {
        onCollect()
    }

    protected open fun onCollect() = Unit

    class DamnedVictims(state: GameState) :
        Upgrade(
            state,
            title = "Damned Victims",
            description = "+10% souls drop\n+10% damned victims spawn."
        ) {
        override fun onCollect() {
            state.soulItemDropMultiplier += 1.1f
            state.totalMonstersSpawnMultiplier += 1.1f
        }
    }

    class Quickie(state: GameState) :
        Upgrade(
            state,
            title = "A Quickie",
            description = "+15% souls drop\n+15% faster spawn of a damned victim."
        ) {
        override fun onCollect() {
            state.soulItemDropMultiplier *= 1.15f
            state.monsterRespawnMultiplier *= 1.15f
        }
    }

    class MakeItRain(state: GameState) :
        Upgrade(
            state,
            title = "Make It Rain",
            description = "+1 additional projectile\n+25% more mob health"
        ) {
        override fun onCollect() {
            state.extraProjectiles++
            state.monsterHealthMultiplier *= 1.25f
        }
    }

    class LargeAndInCharge(state: GameState) :
        Upgrade(
            state,
            title = "Large and In Charge",
            description = "+25% increase projectile radius\n+25% more victims speed"
        ) {
        override fun onCollect() {
            state.projectileDamageRadiusMultiplier *= 1.25f
            state.monsterSpeedMultiplier *= 1.25f
        }
    }

    class ItGoesBoom(state: GameState) :
        Upgrade(
            state,
            title = "It Goes Boom",
            description = "+1 extra explosion of projectile ends\n25% more victims spawn"
        ) {
        override fun onCollect() {
            state.extraExplosions++
            state.totalMonstersSpawnMultiplier *= 1.25f
        }
    }

    class SuperChargeMe(state: GameState) :
        Upgrade(
            state,
            title = "Super Charge Me",
            description = "-5% cooldown reduction\n-25% health"
        ) {
        override fun onCollect() {
            state.skillCDMultiplier *= 0.95f
            state.heroHealthMultiplier *= 0.25f
        }
    }

    class ImFragile(state: GameState) :
        Upgrade(
            state,
            title = "I'm Fragile",
            description = "+3 more damage\nReset health to 1"
        ) {
        override fun onCollect() {
            state.heroHealthMultiplier = 0.25f
            state.extraHeroDamage += 3
        }
    }

    class Juggernaut(state: GameState) :
        Upgrade(
            state,
            title = "Juggernaut",
            description = "+1 more health\n-50% damage\n-25% souls dropped"
        ) {
        override fun onCollect() {
            state.soulItemDropMultiplier *= 0.75f
            state.heroHealthMultiplier += .25f
            state.extraHeroDamage /= 2
        }
    }

    class Brute(state: GameState) :
        Upgrade(
            state,
            title = "Brute",
            description = "+5 more damage\n+50% cooldown"
        ) {
        override fun onCollect() {
            state.skillCDMultiplier *= 1.5f
            state.extraHeroDamage += 5
        }
    }

    class INeedToEat(state: GameState) :
        Upgrade(
            state,
            title = "I Need To Eat",
            description = "+4 health\nLose half your souls"
        ) {
        override fun onCollect() {
            state.heroHealthMultiplier += 1f
            state.soulsCaptured /= 2
        }
    }

    class FillMeUp(state: GameState) :
        Upgrade(
            state,
            title = "Fill Me Up",
            description = "+50% souls drop\n+25% cooldown"
        ) {
        override fun onCollect() {
            state.soulItemDropMultiplier *= 1.5f
            state.skillCDMultiplier *= 1.25f
        }
    }

    class ExtraSpeed(state: GameState) :
        Upgrade(
            state,
            title = "Extra Speed",
            description = "-25% cooldown\nLose all extra projectiles"
        ) {
        override fun onCollect() {
            state.skillCDMultiplier *= 0.75f
            state.extraProjectiles = 0
        }
    }

}

fun createArenaUpgrades(state: GameState) =
    listOf(
        Upgrade.DamnedVictims(state),
        Upgrade.Quickie(state),
        Upgrade.MakeItRain(state),
        Upgrade.LargeAndInCharge(state),
        Upgrade.ItGoesBoom(state),
        Upgrade.SuperChargeMe(state),
        Upgrade.ImFragile(state),
        Upgrade.Juggernaut(state),
        Upgrade.Brute(state),
        Upgrade.INeedToEat(state),
        Upgrade.FillMeUp(state),
        Upgrade.ExtraSpeed(state)
    )