package com.lehaine.game.node.level

import com.lehaine.game.Level
import com.lehaine.game.node.MonsterSpawner
import com.lehaine.game.node.entity.Hero
import com.lehaine.game.node.entity.mob.*
import com.lehaine.game.node.game
import com.lehaine.game.pickOne
import com.lehaine.littlekt.math.floorToInt
import com.lehaine.littlekt.util.datastructure.pool
import com.lehaine.littlekt.util.seconds
import com.lehaine.littlekt.util.signal
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * @author Colton Daily
 * @date 11/4/2022
 */
class TestSpawner(hero: Hero, level: Level) : MonsterSpawner() {
    private val meatBallPool = pool(preallocate = 10) {
        MeatBall(hero, level).apply {
            onDeath += {
                it.reset()
                this@pool.free(it)
            }
        }
    }

    private val chickenSpearPool = pool(preallocate = 10) {
        ChickenSpear(hero, level).apply {
            onDeath += {
                it.reset()
                this@pool.free(it)
            }
        }
    }

    private val hopperManPool = pool(preallocate = 10) {
        HopperMan(hero, level).apply {
            onDeath += {
                it.reset()
                this@pool.free(it)
            }
        }
    }
    private val beetlePool = pool(preallocate = 10) {
        Beetle(hero, level).apply {
            onDeath += {
                it.reset()
                this@pool.free(it)
            }
        }
    }

    private val batPool = pool(preallocate = 10) {
        Bat(hero, level).apply {
            onDeath += {
                it.reset()
                this@pool.free(it)
            }
        }
    }

    var onMajorEvent = signal()

    init {
        addEvent {
            oneTime = false
            endAt = 5.seconds
        }
        addEvent {
            oneTime = false
            startAt = 5.seconds
            endAt = 1.minutes
            actionTimer = { 1000.milliseconds.withRespawnMulti }
            actionCondition = {
                Mob.ALL.size < 250.withMonsterMulti
            }
            action = {
                repeat((2..4).random().withMonsterMulti) {
                    val mob = pickOne(beetlePool, batPool).alloc()

                    mob.apply {
                        teleportToRandomSpotAroundHero()
                    }.also {
                        spawnMob(it)
                    }
                }
            }
            onStart = {
                onMajorEvent.emit()
            }
        }

        addEvent {
            startAt = 1.minutes
            actionCondition = {
                Mob.ALL.size < 250.withMonsterMulti
            }
            action = {
                repeat(5.withMonsterMulti) {
                    val mob = chickenSpearPool.alloc()

                    mob.apply {
                        teleportToRandomSpotAroundHero()
                    }.also {
                        spawnMob(it)
                    }
                }
            }
        }

        addEvent {
            oneTime = false
            startAt = 1.minutes
            endAt = 2.minutes
            actionTimer = { 2.seconds.withRespawnMulti }
            actionCondition = {
                Mob.ALL.size < 250.withMonsterMulti
            }
            action = {
                repeat(6.withMonsterMulti) {
                    val mob = pickOne(meatBallPool, meatBallPool, meatBallPool, hopperManPool).alloc()

                    mob.apply {
                        teleportToRandomSpotAroundHero()
                    }.also {
                        spawnMob(it)
                    }
                }
            }
            onStart = {
                onMajorEvent.emit()
            }
        }

        addEvent {
            oneTime = false
            startAt = 2.minutes
            endAt = 3.minutes
            actionTimer = { 1.seconds.withRespawnMulti }
            actionCondition = {
                Mob.ALL.size < 400.withMonsterMulti
            }
            action = {
                repeat(5.withMonsterMulti) {
                    val mob = pickOne(beetlePool, batPool).alloc()

                    mob.apply {
                        teleportToRandomSpotAroundHero()
                    }.also {
                        spawnMob(it)
                    }
                }
            }
            onStart = {
                onMajorEvent.emit()
            }
        }

        addEvent {
            oneTime = false
            startAt = 3.minutes
            endAt = 5.minutes
            actionTimer = { 500.milliseconds.withRespawnMulti }
            actionCondition = {
                Mob.ALL.size < 400.withMonsterMulti
            }
            action = {
                repeat(5.withMonsterMulti) {
                    val mob = pickOne(beetlePool, batPool, meatBallPool, hopperManPool).alloc()

                    mob.apply {
                        teleportToRandomSpotAroundHero()
                    }.also {
                        spawnMob(it)
                    }
                }
            }
            onStart = {
                onMajorEvent.emit()
            }
        }
        addEvent {
            oneTime = false
            startAt = 5.minutes
            actionTimer = { 100.milliseconds.withRespawnMulti }
            actionCondition = {
                Mob.ALL.size < 1000.withMonsterMulti
            }
            action = {
                repeat(5.withMonsterMulti) {
                    val mob = pickOne(beetlePool, batPool, meatBallPool, hopperManPool, chickenSpearPool).alloc()

                    mob.apply {
                        teleportToRandomSpotAroundHero()
                    }.also {
                        spawnMob(it)
                    }
                }
            }
            onStart = {
                onMajorEvent.emit()
            }
        }
    }

    private val Duration.withRespawnMulti get() = (this.seconds * (game.state.monsterRespawnMultiplier + game.state.unlockIdx * 0.5f)).seconds
    private val Int.withMonsterMulti get() = (this * (game.state.totalMonstersSpawnMultiplier + game.state.unlockIdx * 0.5f)).floorToInt()
}