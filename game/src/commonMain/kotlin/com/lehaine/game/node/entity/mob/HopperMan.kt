package com.lehaine.game.node.entity.mob

import com.lehaine.game.Assets
import com.lehaine.game.Level
import com.lehaine.game.node.entity.Hero
import com.lehaine.game.node.game
import com.lehaine.littlekt.math.geom.cosine
import com.lehaine.littlekt.math.geom.sine
import com.lehaine.rune.engine.node.renderable.entity.angleTo
import com.lehaine.rune.engine.node.renderable.entity.cd
import com.lehaine.rune.engine.node.renderable.entity.distGridTo
import kotlin.math.min
import kotlin.math.sign
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * @author Colton Daily
 * @date 11/16/2022
 */
class HopperMan(hero: Hero, level: Level) : Mob(hero, level) {

    override var speed: Float = 0.05f
    private var xDir = 0f
    private var yDir = 0f

    init {
        sprite.apply {
            registerState(Assets.hopperManAir, 0) { velocityZ != 0f }
            registerState(Assets.hopperManHop, 0) { velocityZ == 0f }
        }
    }

    override fun update(dt: Duration) {
        super.update(dt)
        if (hasEffect(Effect.Stun)) return

        if (velocityZ == 0f && !cd.has("onGround")) {
            velocityZ += 0.75f

            val angle = angleTo(hero)
            xDir = angle.cosine
            yDir = angle.sine

            dir = xDir.sign.toInt()
        }

    }

    override fun fixedUpdate() {
        super.fixedUpdate()
        if (hasEffect(Effect.Stun)) return

        val mul = min(distGridTo(hero) / 7f, 1f)
        if (velocityZ != 0f) {
            velocityX += speed * speedMul * game.state.monsterSpeedMultiplier * xDir * mul
            velocityY += speed * speedMul * game.state.monsterSpeedMultiplier * yDir * mul
        }
    }

    override fun onLand() {
        super.onLand()
        velocityZ = 0f
        cd("onGround", 500.milliseconds)
    }

    override fun onHandOfDeath() {
//        sprite.playOnce(Assets.meatBallHandOfDeath)
//        cd("shake", 700.milliseconds) {
//            hero.camera.shake(100.milliseconds, 2f * Config.cameraShakeMultiplier)
//            fx.meatBallExplode(globalX, globalY)
//            Assets.sfxDeathMob.play(0.2f)
//            spawnDrop()
//        }
//        addEffect(Effect.Stun, Assets.meatBallHandOfDeath.duration)
//        addEffect(Effect.Invincible, Assets.meatBallHandOfDeath.duration)
//        cd("stun", Assets.meatBallHandOfDeath.duration) {
//            die(false)
//        }
    }

    override fun explode() {
        //  fx.meatBallExplode(globalX, globalY)
    }
}