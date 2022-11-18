package com.lehaine.game.node.level

import com.lehaine.game.Level
import com.lehaine.game.node.MonsterSpawner
import com.lehaine.game.node.entity.Hero
import com.lehaine.game.node.entity.mob.ChickenSpear
import com.lehaine.game.node.entity.mob.MeatBall
import com.lehaine.game.node.entity.mob.Mob
import com.lehaine.littlekt.util.datastructure.pool
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

    init {
        addEvent {
            oneTime = false
            endAt = 1.minutes
            actionTimer = 10.seconds
            actionCondition = {
                Mob.ALL.size < 1
            }
            action = {
                repeat(1) {
                    val mob = chickenSpearPool.alloc()

                    mob.apply {
                        teleportToRandomSpotAroundHero()
                    }.also {
                        spawnMob(it)
                    }
                }
            }
        }
    }
}