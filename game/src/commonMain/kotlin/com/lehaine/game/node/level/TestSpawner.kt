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

    init {
        addEvent {
            oneTime = false
            endAt = 30.seconds
            actionTimer = { 200.milliseconds.withRespawnMulti }
            actionCondition = {
                Mob.ALL.size < 500.withMonsterMulti
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
        }

        addEvent {
            actionCondition = {
                Mob.ALL.size < 100.withMonsterMulti

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
            endAt = 2.minutes
            actionTimer = { 10.seconds.withRespawnMulti }
            actionCondition = {
                Mob.ALL.size < 100.withMonsterMulti
            }
            action = {
                repeat(3.withMonsterMulti) {
                    val mob = hopperManPool.alloc()

                    mob.apply {
                        teleportToRandomSpotAroundHero()
                    }.also {
                        spawnMob(it)
                    }
                }
            }
        }
    }

    private val Duration.withRespawnMulti get() = (this.seconds * game.state.monsterRespawnMultiplier).seconds
    private val Int.withMonsterMulti get() = (this * game.state.totalMonstersSpawnMultiplier).floorToInt()
}