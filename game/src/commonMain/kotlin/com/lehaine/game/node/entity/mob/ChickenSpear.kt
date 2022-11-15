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
import kotlin.time.Duration.Companion.milliseconds

/**
 * @author Colton Daily
 * @date 11/15/2022
 */
class ChickenSpear(hero: Hero, level: Level) : Mob(hero, level) {

    override var speed: Float = 0.022f
    private var xDir = 0f
    private var yDir = 0f

    init {
        sprite.apply {
            registerState(Assets.chickenSpearRun, 0) { !cd.has("stun") }
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

    override fun onLevelCollision(xDir: Int, yDir: Int) {
        super.onLevelCollision(xDir, yDir)

        if (xDir != 0) {
            this.xDir = -this.xDir
            dir = this.xDir.sign.toInt()
        }
        if (yDir != 0) {
            this.yDir = -this.yDir
        }
    }

    override fun handleHandOfDeath() {
        sprite.playOnce(Assets.chickenSpearHandOfDeath)
        cd("shake", 700.milliseconds) {
            hero.camera.shake(100.milliseconds, 2f * Config.cameraShakeMultiplier)
            fx.chickenExplode(globalX, globalY)
            spawnDrop()
        }
        addEffect(Effect.Stun, Assets.chickenSpearHandOfDeath.duration)
        addEffect(Effect.Invincible, Assets.chickenSpearHandOfDeath.duration)
        cd("stun", Assets.chickenSpearHandOfDeath.duration) {
            die(false)
        }
    }

    override fun explode() {
        fx.meatBallExplode(globalX, globalY)
    }
}
