package com.lehaine.game.node.entity

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.node.entity.mob.Mob
import com.lehaine.game.node.fx
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
    private val speed = 0.25f
    private val knockbackPower = 0.1f
    private var attacked = false

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
        var hit = false
        Mob.ALL.fastForEach {
            val colliding = isCollidingWith(it)
            if (it.enabled && colliding) {
                hit = true
                it.hit(hero.damage, hero.angleTo(it))

                val angle = hero.angleTo(it)
                it.velocityX += knockbackPower * angle.cosine
                it.velocityY += knockbackPower * angle.sine
            }
        }


        if (hit || (!cd.has("attacked") && attacked)) {
            fx.spiritBallExplode(centerX, centerY)
            attacked = false
            enabled = false
            hero.projectileFinished(this)
        }
    }

    override fun onLevelCollision(xDir: Int, yDir: Int) {
        super.onLevelCollision(xDir, yDir)
        cd.remove("attacked")
        fx.spiritBallExplode(centerX, centerY)
        attacked = false
        enabled = false
        hero.projectileFinished(this)
    }

    fun moveTowardsAngle(angle: Angle) {
        velocityX = angle.cosine * speed
        velocityY = angle.sine * speed
    }
}