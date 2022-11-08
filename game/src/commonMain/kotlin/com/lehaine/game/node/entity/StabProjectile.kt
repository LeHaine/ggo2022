package com.lehaine.game.node.entity

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.node.entity.mob.Mob
import com.lehaine.littlekt.math.distSqr
import com.lehaine.littlekt.math.geom.cosine
import com.lehaine.littlekt.math.geom.sine
import com.lehaine.littlekt.util.fastForEach
import com.lehaine.rune.engine.node.renderable.entity.Entity
import com.lehaine.rune.engine.node.renderable.entity.angleTo
import com.lehaine.rune.engine.node.renderable.entity.cd
import kotlin.time.Duration

/**
 * @author Colton Daily
 * @date 11/4/2022
 */
class StabProjectile(val hero: Hero) : Entity(Config.GRID_CELL_SIZE.toFloat()), Projectile {
    private var attacked = false


    val knockbackPower = 0.1f

    init {
        anchorX = 0.5f
        anchorY = 0.5f
        width = 64f
        height = 16f
    }

    override fun update(dt: Duration) {
        super.update(dt)
        if (!attacked) {
            sprite.playOnce(Assets.stabAttack1)
            cd("attacked", Assets.stabAttack1.duration)
            attacked = true
            Mob.ALL.fastForEach {
                val colliding = isCollidingWith(it)
                if (it.enabled && colliding) {
                    it.hit(hero.damage, hero.angleTo(it))

                    val angle = hero.angleTo(it)
                    it.velocityX += knockbackPower * angle.cosine
                    it.velocityY += knockbackPower * angle.sine
                }
            }
        }
        if (!cd.has("attacked") && attacked) {
            attacked = false
            enabled = false
            hero.projectileFinished(this)
        }
    }
}