package com.lehaine.game

/**
 * @author Colton Daily
 * @date 11/10/2022
 */
class GameState {

    var totalMonstersSpawnMultiplier = 1f
    var monsterHealthMultiplier = 1f
    var monsterRespawnMultiplier = 1f
    var monsterSpeedMultiplier = 1f

    var soulCollectibleDropMultiplier = 1f

    var quotasFailed = 0
    var soulsCaptured = 0


    val soulsRequired = listOf(100, 200, 300, 400)

    var unlockIdx = 0
    val unlocks = Array(4) { false }


    val nextUnlockCost get() = soulsRequired[unlockIdx]
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