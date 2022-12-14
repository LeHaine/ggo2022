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

/**
 * @author Colton Daily
 * @date 11/21/2022
 */
class Bat(hero: Hero, level: Level) : Mob(hero, level) {

    override var speed: Float = 0.005f
    private var xDir = 0f
    private var yDir = 0f

    init {
        width = 11f
        height = 6f
        zr = 0.5f
        sprite.apply {
            registerState(Assets.batFlap, 0) { !cd.has("stun") }
        }
    }

    override fun update(dt: Duration) {
        super.update(dt)
        val angle = angleTo(hero)
        xDir = angle.cosine
        yDir = angle.sine

        dir = xDir.sign.toInt()

        if (zr > 0.5f) {
            gravity = 0.05f
        } else {
            zr = 0.5f
            gravity = 0f
        }
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
            fx.batExplode(globalX, globalY)
            spawnDrop()
        }
        addEffect(Effect.Stun, Assets.chickenSpearHandOfDeath.duration)
        addEffect(Effect.Invincible, Assets.chickenSpearHandOfDeath.duration)
        cd("stun", Assets.chickenSpearHandOfDeath.duration) {
            die(false)
        }
    }

    override fun explode() {
        fx.batExplode(globalX, globalY)
    }
}
