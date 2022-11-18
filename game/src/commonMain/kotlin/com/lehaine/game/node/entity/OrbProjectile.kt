package com.lehaine.game.node.entity

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.node.entity.mob.Mob
import com.lehaine.game.node.fx
import com.lehaine.game.node.game
import com.lehaine.littlekt.math.distSqr
import com.lehaine.littlekt.math.geom.Angle
import com.lehaine.littlekt.math.geom.cosine
import com.lehaine.littlekt.math.geom.sine
import com.lehaine.littlekt.util.fastForEach
import com.lehaine.rune.engine.GameLevel
import com.lehaine.rune.engine.node.renderable.entity.ObliqueEntity
import com.lehaine.rune.engine.node.renderable.entity.angleTo
import com.lehaine.rune.engine.node.renderable.entity.cd
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * @author Colton Daily
 * @date 11/9/2022
 */
class OrbProjectile(val hero: Hero, level: GameLevel<*>) : ObliqueEntity(level, Config.GRID_CELL_SIZE.toFloat()),
    Projectile {
    private val speed = 0.6f
    private val knockbackPower = 0.1f
    private var attacked = false
    private var totalWallHitsLeft = 3

    init {
        sprite.slice = Assets.atlas.getByPrefix("spiritOrb").slice
        anchorX = 0.5f
        anchorY = 0.5f
        frictionX = 1f
        frictionY = 1f
        width = 6f
        height = 6f
    }

    override fun update(dt: Duration) {
        super.update(dt)
        if (!attacked) {
            cd("attacked", (500.800).milliseconds)
            attacked = true
        }

        val camera = hero.camera.camera ?: return
        val vw = camera.virtualWidth
        val vx = camera.position.x - vw * 0.5f
        val vx2 = vx + vw - hero.camera.offset.x * 2

        if ((left <= vx && velocityX < 0) || (right >= vx2 && velocityX > 0)) {
            velocityX = -velocityX
            totalWallHitsLeft--
        }


        var hit = false
        Mob.ALL.fastForEach {
            val dist = outerRadius * game.state.projectileDamageRadiusMultiplier + it.outerRadius
            val colliding = distSqr(px, py, it.px, it.py) <= dist * dist
            if (it.enabled && colliding) {
                hit = true
                it.hit(hero.angleTo(it))

                val angle = hero.angleTo(it)
                it.velocityX += knockbackPower * angle.cosine
                it.velocityY += knockbackPower * angle.sine
            }
        }


        if (hit && attacked) {
            fx.spiritBallExplode(centerX, centerY)
            attacked = false
            enabled = false
            hero.projectileFinished(this)
        }
    }

    override fun onLevelCollision(xDir: Int, yDir: Int) {
        super.onLevelCollision(xDir, yDir)
        if (totalWallHitsLeft <= 0) {
            cd.remove("attacked")
            fx.spiritBallExplode(centerX, centerY)
            attacked = false
            enabled = false
            totalWallHitsLeft = 3
            hero.projectileFinished(this)
        } else {
            if (xDir != 0) {
                velocityX = -velocityX * 2f
            }
            if (yDir != 0) {
                velocityY = -velocityY * 2f
            }
            totalWallHitsLeft--
        }
    }

    fun moveTowardsAngle(angle: Angle) {
        velocityX = angle.cosine * speed
        velocityY = angle.sine * speed
    }
}