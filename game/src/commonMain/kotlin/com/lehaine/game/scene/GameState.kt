package com.lehaine.game.scene

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

    var shootingUnlocked = false
    var dashUnlocked = false
    var boneSpearUnlocked = false
    var handOfDeathUnlocked = false
}