package com.lehaine.game.node.entity

import com.lehaine.game.Config
import com.lehaine.game.node.entity.mob.Mob
import com.lehaine.game.node.fx
import com.lehaine.game.node.game
import com.lehaine.littlekt.math.distSqr
import com.lehaine.littlekt.math.geom.cosine
import com.lehaine.littlekt.math.geom.sine
import com.lehaine.littlekt.util.fastForEach
import com.lehaine.rune.engine.node.renderable.entity.Entity
import com.lehaine.rune.engine.node.renderable.entity.angleTo
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * @author Colton Daily
 * @date 11/20/2022
 */
class ExplodingProjectile(val hero: Hero) : Entity(Config.GRID_CELL_SIZE.toFloat()), Projectile {
    private val knockbackPower get() = 0.25f * game.state.projectileKnockbackMultiplier
    private var attacked = false

    init {
        anchorX = 0.5f
        anchorY = 0.5f
        width = 32f
        height = 32f
    }

    override fun update(dt: Duration) {
        super.update(dt)
        if (!attacked) {
            attacked = true
            fx.smallExplosion(centerX, centerY)
            hero.camera.shake(50.milliseconds, 0.2f * Config.cameraShakeMultiplier)
        }

        Mob.ALL.fastForEach {
            val dist = outerRadius * game.state.projectileDamageRadiusMultiplier + it.outerRadius
            val colliding = distSqr(px, py, it.px, it.py) <= dist * dist
            if (it.enabled && colliding) {
                it.hit(hero.angleTo(it))

                val angle = hero.angleTo(it)
                it.velocityX += knockbackPower * angle.cosine
                it.velocityY += knockbackPower * angle.sine
            }
        }

        if (attacked) {
            attacked = false
            enabled = false
            hero.projectileFinished(this)

        }
    }
}