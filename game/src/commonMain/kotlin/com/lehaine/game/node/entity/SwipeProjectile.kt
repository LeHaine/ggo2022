package com.lehaine.game.node.entity

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.node.entity.mob.Mob
import com.lehaine.game.node.game
import com.lehaine.littlekt.math.distSqr
import com.lehaine.littlekt.math.geom.cosine
import com.lehaine.littlekt.math.geom.sine
import com.lehaine.littlekt.util.fastForEach
import com.lehaine.rune.engine.node.renderable.entity.Entity
import com.lehaine.rune.engine.node.renderable.entity.angleTo
import com.lehaine.rune.engine.node.renderable.entity.cd
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * @author Colton Daily
 * @date 11/4/2022
 */
class SwipeProjectile(val hero: Hero) : Entity(Config.GRID_CELL_SIZE.toFloat()), Projectile {
    private var swiped = false
    private val knockbackPower get() = 0.25f * game.state.projectileKnockbackMultiplier

    init {
        anchorX = 0.5f
        anchorY = 0.5f
        width = 48f
        height = 48f
    }

    override fun update(dt: Duration) {
        super.update(dt)
        if (!swiped) {
            val swipeAnim = swipeAttacks.random()
            sprite.playOnce(swipeAnim)
            cd("swipe", swipeAnim.duration)
            swiped = true
            Mob.ALL.fastForEach {
                val dist = outerRadius * game.state.projectileDamageRadiusMultiplier + it.outerRadius
                val colliding = distSqr(px, py, it.px, it.py) <= dist * dist
                if (it.enabled && colliding) {
                    it.hit(hero.angleTo(it))

                    val angle = hero.angleTo(it)
                    it.velocityX += knockbackPower * angle.cosine
                    it.velocityY += knockbackPower * angle.sine
                    it.velocityZ += knockbackPower * 0.5f
                    hero.camera.shake(100.milliseconds, 0.5f * Config.cameraShakeMultiplier)
                }
            }
        }
        if (!cd.has("swipe") && swiped) {
            swiped = false
            enabled = false
            hero.projectileFinished(this)
        }
    }

    companion object {
        private val swipeAttacks by lazy { listOf(Assets.swipeAttack1, Assets.swipeAttack2, Assets.swipeAttack3) }
    }
}