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
            description = "+10% souls drop\n+10% mobs spawn."
        ) {
        override fun onCollect() {
            state.soulItemDropMultiplier += 0.1f
            state.totalMonstersSpawnMultiplier += 0.1f
        }
    }

    class Quickie(state: GameState) :
        Upgrade(
            state,
            title = "A Quickie",
            description = "+15% souls drop\n+15% faster spawn of a mob."
        ) {
        override fun onCollect() {
            state.soulItemDropMultiplier += 0.15f
            state.monsterRespawnMultiplier *= 0.85f
        }
    }

    class MakeItRain(state: GameState) :
        Upgrade(
            state,
            title = "Make It Rain",
            description = "+1 additional projectile\n+25% mob health"
        ) {
        override fun onCollect() {
            state.extraProjectiles++
            state.monsterHealthMultiplier += 0.25f
        }
    }

    class LargeAndInCharge(state: GameState) :
        Upgrade(
            state,
            title = "Large and In Charge",
            description = "+25% increase area of effect\n+25% more mobs speed"
        ) {
        override fun onCollect() {
            state.projectileDamageRadiusMultiplier += 0.25f
            state.monsterSpeedMultiplier += 0.25f
        }
    }

    class ItGoesBoom(state: GameState) :
        Upgrade(
            state,
            title = "It Goes Boom",
            description = "+1 extra explosion of projectile ends\n25% more mobs spawn"
        ) {
        override fun onCollect() {
            state.extraExplosions++
            state.totalMonstersSpawnMultiplier += 0.25f
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
            state.heroBaseHealth++
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
            state.skillCDMultiplier += 0.5f
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
            state.heroBaseHealth += 4
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
            state.soulItemDropMultiplier += 0.5f
            state.skillCDMultiplier += 0.25f
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
            state.heroSpeedMultiplier += 0.25f
            state.skillCDMultiplier *= 0.95f
            state.extraExplosions = 0
        }
    }

    class GodLike(state: GameState) :
        Upgrade(
            state,
            title = "God Like",
            description = "-30% cooldown\nHealth is set to 1"
        ) {
        override fun onCollect() {
            state.skillCDMultiplier *= 0.7f
            state.heroHealthMultiplier = 1f
            state.heroBaseHealth = 1
        }
    }

    class RepeatAfterMe(state: GameState) :
        Upgrade(
            state,
            title = "Repeat After Me",
            description = "+1 attack repeated\n+75% mob health"
        ) {
        override fun onCollect() {
            state.extraHeroAttacks++
            state.monsterHealthMultiplier += 0.75f
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
            state.monsterHealthMultiplier *= 0.75f
        }
    }

    class ILoveSouls(state: GameState) :
        Upgrade(
            state,
            title = "I Love Souls",
            description = "+100% souls dropped\n+50% mob speed"
        ) {
        override fun onCollect() {
            state.soulItemDropMultiplier += 1f
            state.monsterSpeedMultiplier += 0.5f
        }
    }

    class ALittleOffTheTop(state: GameState) :
        Upgrade(
            state,
            title = "A Little of the Top",
            description = "+2 extra projectiles\n+100% mobs spawned"
        ) {
        override fun onCollect() {
            state.totalMonstersSpawnMultiplier += 1f
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
            state.totalMonstersSpawnMultiplier += 0.5f
            state.monsterRespawnMultiplier += 0.5f
            state.monsterSpeedMultiplier += 0.5f
            state.monsterHealthMultiplier += 0.5f

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

    class KnockKnock(state: GameState) :
        Upgrade(
            state,
            title = "Knock Knock",
            description = "+10% knock back\n+10% mob health"
        ) {
        override fun onCollect() {
            state.projectileKnockbackMultiplier += 0.1f
            state.monsterHealthMultiplier += 0.1f
        }
    }

    class ImACannon(state: GameState) :
        Upgrade(
            state,
            title = "I'm a Cannon",
            description = "+25% knock back\n+1 extra explosion\n+25% mob speed\n+25% mob health"
        ) {
        override fun onCollect() {
            state.projectileKnockbackMultiplier += 0.25f
            state.extraExplosions++
            state.monsterSpeedMultiplier += 0.25f
            state.monsterHealthMultiplier += 0.25f
        }
    }

    class Weak(state: GameState) :
        Upgrade(
            state,
            title = "Weak",
            description = "+2 damage\n+1 attack repeated\n-50% knock back"
        ) {
        override fun onCollect() {
            state.extraHeroDamage += 2
            state.extraHeroAttacks++
            state.projectileKnockbackMultiplier *= 0.5f
        }
    }

    class NeverMiss(state: GameState) :
        Upgrade(
            state,
            title = "Never Miss",
            description = "+50% area of effect\n-3 damage"
        ) {
        override fun onCollect() {
            state.projectileDamageRadiusMultiplier += 0.5f
            state.extraHeroDamage -= 3
        }
    }

    class MoreMoreMore(state: GameState) :
        Upgrade(
            state,
            title = "More More More!",
            description = "+1 extra projectile\n+1 extra explosion\n+15% all mob stats"
        ) {
        override fun onCollect() {
            state.extraProjectiles++
            state.extraExplosions++

            state.totalMonstersSpawnMultiplier += 0.15f
            state.monsterRespawnMultiplier += 0.15f
            state.monsterSpeedMultiplier += 0.15f
            state.monsterHealthMultiplier += 0.15f
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
        Upgrade.SlowItDown(state),
        Upgrade.KnockKnock(state),
        Upgrade.ImACannon(state),
        Upgrade.Weak(state),
        Upgrade.NeverMiss(state),
        Upgrade.MoreMoreMore(state)
    )