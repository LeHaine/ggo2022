package com.lehaine.game

import com.lehaine.game.data.ExpTable

/**
 * @author Colton Daily
 * @date 11/10/2022
 */
class GameState {

    var totalMonstersSpawnMultiplier = 1f
        set(value) {
            field = value.coerceAtMost(7f)
        }
    var monsterHealthMultiplier = 1f
    var monsterRespawnMultiplier = 1f
    var monsterSpeedMultiplier = 1f

    var skillCDMultiplier = 1f
    var soulItemDropMultiplier = 1f
        set(value) {
            field = value.coerceAtMost(10f)
        }
    var extraProjectiles = 0
    var extraExplosions = 0
    var projectileDamageRadiusMultiplier = 1f

    var heroBaseHealth = 6
    var heroHealthMultiplier = 1f
    var heroSpeedMultiplier = 1f
    var projectileKnockbackMultiplier = 1f
    var extraHeroDamage = 0
        set(value) {
            field = value.coerceAtLeast(0)
        }
    var extraHeroAttacks = 0
        set(value) {
            field = value.coerceAtLeast(0)
        }


    var onHealHero: () -> Unit = {}

    var quotasFailed = 0
    var soulsCaptured = 0

    val exp = ExpTable()

    val soulsRequired = listOf(150, 1500, 5000, 25000)

    var unlockIdx = 0
    val unlocks = Array(4) { false }


    val nextUnlockCost get() = if (soulsRequired.indices.contains(unlockIdx)) soulsRequired[unlockIdx] else 1000000
    var shootingUnlocked
        get() = unlocks[0]
        set(value) {
            unlocks[0] = value
        }
    var dashUnlocked
        get() = unlocks[1]
        set(value) {
            unlocks[1] = value
        }
    var boneSpearUnlocked
        get() = unlocks[2]
        set(value) {
            unlocks[2] = value
        }
    var handOfDeathUnlocked
        get() = unlocks[3]
        set(value) {
            unlocks[3] = value
        }

    fun unlockNextSkill() {
        unlocks[unlockIdx++] = true
    }
}