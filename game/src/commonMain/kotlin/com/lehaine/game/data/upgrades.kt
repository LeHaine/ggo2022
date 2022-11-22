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
            state.monsterRespawnMultiplier *= 0.85f
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
            description = "+25% increase area of effect\n+25% more victims speed"
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
            state.heroHealthMultiplier *= 0.75f
        }
    }

    class ImFragile(state: GameState) :
        Upgrade(
            state,
            title = "I'm Fragile",
            description = "+3 more damage\n-50% health"
        ) {
        override fun onCollect() {
            state.heroHealthMultiplier *= 0.5f
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

    class Runner(state: GameState) :
        Upgrade(
            state,
            title = "Runner",
            description = "+25% run speed\n-5% cooldown\nLose all extra explosions"
        ) {
        override fun onCollect() {
            state.skillCDMultiplier *= 0.95f
            state.extraExplosions = 0
        }
    }

    class GodLike(state: GameState) :
        Upgrade(
            state,
            title = "God Like",
            description = "-75% cooldown\nHealth is stuck at 2 and cannot be increased"
        ) {
        override fun onCollect() {
            state.skillCDMultiplier *= 0.25f
            state.heroHealthMultiplier = 0.5f
            state.lockHeroHealth = true
        }
    }

    class RepeatAfterMe(state: GameState) :
        Upgrade(
            state,
            title = "Repeat After Me",
            description = "+1 attack repeated\nMob health increased by 75%"
        ) {
        override fun onCollect() {
            state.extraHeroAttacks++
            state.monsterHealthMultiplier *= 1.75f
        }
    }

    class MakeEmWeak(state: GameState) :
        Upgrade(
            state,
            title = "Make 'em Weak",
            description = "-25% mob health\n-1 attack repeated"
        ) {
        override fun onCollect() {
            state.extraHeroAttacks--
            state.monsterHealthMultiplier *= 1.75f
        }
    }

    class ILoveSouls(state: GameState) :
        Upgrade(
            state,
            title = "I Love Souls",
            description = "+100% souls dropped\nYour max health can no longer change"
        ) {
        override fun onCollect() {
            state.soulItemDropMultiplier *= 2f
            state.lockHeroHealth = true
        }
    }

    class ALittleOffTheTop(state: GameState) :
        Upgrade(
            state,
            title = "A Little of the Top",
            description = "+2 extra projectiles\n+100% more mobs spawned"
        ) {
        override fun onCollect() {
            state.totalMonstersSpawnMultiplier *= 2f
            state.extraProjectiles += 2
        }
    }

    class JustALittleReset(state: GameState) :
        Upgrade(
            state,
            title = "Just a Little Reset",
            description = "Reset all hero stats back to default\n+50% all mob stats"
        ) {
        override fun onCollect() {
            state.totalMonstersSpawnMultiplier *= 1.5f
            state.monsterRespawnMultiplier *= 0.5f
            state.monsterSpeedMultiplier *= 1.5f
            state.monsterHealthMultiplier *= 1.5f

            state.lockHeroHealth = false

            state.skillCDMultiplier = 1f
            state.soulItemDropMultiplier = 1f
            state.extraProjectiles = 0
            state.extraExplosions = 0
            state.projectileDamageRadiusMultiplier = 1f
            state.heroHealthMultiplier = 1f
            state.extraHeroDamage = 0
            state.extraHeroAttacks = 0
        }
    }

    class HealMe(state: GameState) :
        Upgrade(
            state,
            title = "Heal Me",
            description = "Heal to full health\n-50% souls drop"
        ) {
        override fun onCollect() {
            state.soulItemDropMultiplier *= 0.5f
            state.onHealHero()
        }
    }

    class SlowItDown(state: GameState) :
        Upgrade(
            state,
            title = "Slow it Down",
            description = "-25% mob speed\n-5% hero speed"
        ) {
        override fun onCollect() {
            state.monsterSpeedMultiplier *= 0.75f
            state.heroSpeedMultiplier *= 0.95f
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
        Upgrade.ExtraSpeed(state),
        Upgrade.Runner(state),
        Upgrade.GodLike(state),
        Upgrade.RepeatAfterMe(state),
        Upgrade.MakeEmWeak(state),
        Upgrade.ILoveSouls(state),
        Upgrade.ALittleOffTheTop(state),
        Upgrade.JustALittleReset(state),
        Upgrade.HealMe(state),
        Upgrade.SlowItDown(state)
    )