package com.lehaine.game

import com.lehaine.game.data.ExpTable

/**
 * @author Colton Daily
 * @date 11/10/2022
 */
class GameState {

    val totalMonstersSpawnMultiplier get() = 1f + arenaState.totalMonstersSpawnMultiplier
    val monsterHealthMultiplier get() = 1f + arenaState.monsterHealthMultiplier
    val monsterRespawnMultiplier get() = 1f + arenaState.monsterRespawnMultiplier
    val monsterSpeedMultiplier get() = 1f + arenaState.monsterSpeedMultiplier

    val skillCDMultiplier get() = 1f + arenaState.skillCDMultiplier
    val soulItemDropMultiplier get() = 1f + arenaState.soulItemDropMultiplier
    val extraProjectiles get() = arenaState.extraProjectiles
    val extraExplosions get() = arenaState.extraExplosions
    val projectileDamageRadiusMultiplier get() = 1f + arenaState.projectileDamageRadiusMultiplier
    val heroHealthMultiplier get() = 1f + arenaState.heroHealthMultiplier
    val extraHeroDamage get() = arenaState.extraHeroDamage

    val arenaState = ArenaState()
    val officeState = OfficeState()

    var quotasFailed = 0
    var soulsCaptured = 0

    val exp = ExpTable()

    val soulsRequired = listOf(100, 200, 300, 400)

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

    class ArenaState {
        var totalMonstersSpawnMultiplier = 0f
        var monsterHealthMultiplier = 0f
        var monsterRespawnMultiplier = 0f
        var monsterSpeedMultiplier = 0f

        var skillCDMultiplier = 0f
        var soulItemDropMultiplier = 0f
        var extraProjectiles = 0
        var extraExplosions = 0
        var projectileDamageRadiusMultiplier = 0f
        var heroHealthMultiplier = 0f
        var extraHeroDamage = 0

        fun reset() {
            totalMonstersSpawnMultiplier = 0f
            monsterHealthMultiplier = 0f
            monsterRespawnMultiplier = 0f
            monsterSpeedMultiplier = 0f

            skillCDMultiplier = 0f
            soulItemDropMultiplier = 0f
            extraProjectiles = 0
            extraExplosions = 0
            projectileDamageRadiusMultiplier = 0f
            heroHealthMultiplier = 0f
            extraHeroDamage = 0
        }
    }

    class OfficeState {

    }
}