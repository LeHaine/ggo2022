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

    sealed class Arena(state: GameState, title: String, description: String) : Upgrade(state, title, description) {
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
            }
        }

        class SuperChargeMe(state: GameState) :
            Upgrade(
                state,
                title = "Super Charge Me",
                description = "-50% cooldown reduction\n-50% health"
            ) {
            override fun onCollect() {
                state.skillCDMultiplier *= 0.5f
                state.heroHealthMultiplier *= 0.5f
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
                state.heroHealthMultiplier += 1
                state.extraHeroDamage /= 2
            }
        }
    }
}

fun createArenaUpgrades(state: GameState) =
    listOf(
        Upgrade.Arena.DamnedVictims(state),
        Upgrade.Arena.Quickie(state),
        Upgrade.Arena.MakeItRain(state),
        Upgrade.Arena.LargeAndInCharge(state),
        Upgrade.Arena.ItGoesBoom(state),
        Upgrade.Arena.SuperChargeMe(state),
        Upgrade.Arena.ImFragile(state),
        Upgrade.Arena.Juggernaut(state)
    )