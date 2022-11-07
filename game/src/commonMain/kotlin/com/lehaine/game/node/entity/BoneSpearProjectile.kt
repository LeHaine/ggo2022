package com.lehaine.game.node.entity

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.node.entity.mob.Mob
import com.lehaine.game.node.fx
import com.lehaine.littlekt.math.distSqr
import com.lehaine.littlekt.util.fastForEach
import com.lehaine.rune.engine.node.renderable.entity.Entity
import com.lehaine.rune.engine.node.renderable.entity.cd
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * @author Colton Daily
 * @date 11/6/2022
 */
class BoneSpearProjectile(val hero: Hero) : Entity(Config.GRID_CELL_SIZE.toFloat()), Projectile {
    private var attacked = false

    init {
        anchorX = 0.5f
        anchorY = 1f
        width = 32f
        height = 32f

        sprite.onFrameChanged += { frameIdx ->
            if (frameIdx == 9) {
                fx.groundParticles(globalX, globalY)
                hero.camera.shake(100.milliseconds)
                Mob.ALL.fastForEach {
                    val dist = outerRadius + it.outerRadius
                    val colliding = distSqr(px, py, it.px, it.py) <= dist * dist
                    if (it.enabled && colliding) {
                        it.explode()
                        it.die()
                    }
                }
            }
        }
    }

    override fun update(dt: Duration) {
        super.update(dt)
        if (!attacked) {
            fx.shadowSmall(globalX, globalY, Assets.boneSpearAttack.duration)
            sprite.playOnce(Assets.boneSpearAttack)
            cd("attack", Assets.boneSpearAttack.duration)
            attacked = true
        }
        if (!cd.has("attack") && attacked) {
            attacked = false
            enabled = false
            hero.projectileFinished(this)
        }
    }
}