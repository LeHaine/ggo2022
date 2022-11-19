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
                description = "+10% souls drop & +10% damned victims spawn."
            ) {
            override fun onCollect() {
                state.arenaState.soulItemDropMultiplier += 0.1f
                state.arenaState.totalMonstersSpawnMultiplier += 0.1f
            }
        }

        class Quickie(state: GameState) :
            Upgrade(
                state,
                title = "A Quickie",
                description = "+15% souls drop & +15% faster spawn of a damned victim."
            ) {
            override fun onCollect() {
                state.arenaState.soulItemDropMultiplier += 0.15f
                state.arenaState.monsterRespawnMultiplier += 0.15f
            }
        }

        class MakeItRain(state: GameState) :
            Upgrade(
                state,
                title = "Make It Rain",
                description = "+1 additional projectile & +25% more mob health"
            ) {
            override fun onCollect() {
                state.arenaState.extraProjectiles++
            }
        }

        class LargeAndInCharge(state: GameState) :
            Upgrade(
                state,
                title = "Large and In Charge",
                description = "+25% increase projectile radius & +25% more victims speed"
            ) {
            override fun onCollect() {
                state.arenaState.projectileDamageRadiusMultiplier += 0.5f
            }
        }

        class ItGoesBoom(state: GameState) :
            Upgrade(
                state,
                title = "It Goes Boom",
                description = "+1 extra explosion of projectile ends & 25% more victims spawn"
            ) {
            override fun onCollect() {
                state.arenaState.extraExplosions++
            }
        }

        class SuperChargeMe(state: GameState) :
            Upgrade(
                state,
                title = "Super Charge Me",
                description = "-50% cooldown reduction & -50% health"
            ) {
            override fun onCollect() {
                state.arenaState.skillCDMultiplier *= 0.5f
                state.arenaState.heroHealthMultiplier *= 0.5f
            }
        }

        class ImFragile(state: GameState) :
            Upgrade(
                state,
                title = "I'm Fragile",
                description = "+200% more damage & reset health to 1"
            ) {
            override fun onCollect() {
                state.arenaState.heroHealthMultiplier = 0.25f
                state.arenaState.extraHeroDamage *= 3
            }
        }

        class Juggernaut(state: GameState) :
            Upgrade(
                state,
                title = "Juggernaut",
                description = "+100% more health & damage reduce by 50% and souls by 25%"
            ) {
            override fun onCollect() {
                state.arenaState.soulItemDropMultiplier -= 0.25f
                state.arenaState.heroHealthMultiplier *= 2f
                state.arenaState.extraHeroDamage /= 2
            }
        }
    }

    sealed class Office(state: GameState, title: String, description: String) : Upgrade(state, title, description) {

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

fun createOfficeUpgrades(state: GameState) = listOf<Upgrade.Office>()