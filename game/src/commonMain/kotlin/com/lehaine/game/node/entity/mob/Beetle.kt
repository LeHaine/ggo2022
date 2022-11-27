package com.lehaine.game.node.entity.mob

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.Level
import com.lehaine.game.node.entity.Hero
import com.lehaine.game.node.fx
import com.lehaine.game.node.game
import com.lehaine.littlekt.math.geom.cosine
import com.lehaine.littlekt.math.geom.sine
import com.lehaine.rune.engine.node.renderable.entity.angleTo
import com.lehaine.rune.engine.node.renderable.entity.cd
import kotlin.math.sign
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * @author Colton Daily
 * @date 11/21/2022
 */
class Beetle(hero: Hero, level: Level) : Mob(hero, level) {

    override val baseHealth: Int = 1
    override var speed: Float = 0.01f
    private var xDir = 0f
    private var yDir = 0f

    init {
        width = 11f
        height = 5f
        shadow.apply {
           slice = Assets.atlas.getByPrefix("shadowSmall").slice
        }
        sprite.apply {
            registerState(Assets.beetleRun, 0) { !cd.has("stun") }
        }
    }

    override fun update(dt: Duration) {
        super.update(dt)
        if (!cd.has("redirect")) {
            cd("redirect", 5.seconds) {
                val angle = angleTo(hero)
                xDir = angle.cosine
                yDir = angle.sine

                dir = xDir.sign.toInt()
            }
        }
    }

    override fun onSpawn() {
        super.onSpawn()
        val angle = angleTo(hero)
        xDir = angle.cosine
        yDir = angle.sine

        dir = xDir.sign.toInt()
    }

    override fun fixedUpdate() {
        super.fixedUpdate()
        if (hasEffect(Effect.Stun)) return

        velocityX += speed * speedMul * game.state.monsterSpeedMultiplier * xDir
        velocityY += speed * speedMul * game.state.monsterSpeedMultiplier * yDir
    }

    override fun onHandOfDeath() {
        sprite.playOnce(Assets.chickenSpearHandOfDeath)
        cd("shake", 700.milliseconds) {
            Assets.sfxDeathMob.play(0.2f * Config.sfxMultiplier)
            hero.camera.shake(100.milliseconds, 2f * Config.cameraShakeMultiplier)
            fx.beetleExplode(globalX, globalY)
            spawnDrop()
        }
        addEffect(Effect.Stun, Assets.chickenSpearHandOfDeath.duration)
        addEffect(Effect.Invincible, Assets.chickenSpearHandOfDeath.duration)
        cd("stun", Assets.chickenSpearHandOfDeath.duration) {
            die(false)
        }
    }

    override fun explode() {
        fx.beetleExplode(globalX, globalY)
    }
}
