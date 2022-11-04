package com.lehaine.game.entity

import com.lehaine.game.Config
import com.lehaine.littlekt.math.geom.Angle
import com.lehaine.littlekt.util.signal1v
import com.lehaine.rune.engine.GameLevel
import com.lehaine.rune.engine.node.renderable.entity.LevelEntity
import com.lehaine.rune.engine.node.renderable.entity.cd
import com.lehaine.rune.engine.node.renderable.entity.toGridPosition
import kotlin.math.ceil
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * @author Colton Daily
 * @date 11/4/2022
 */
open class Mob(val hero: Hero, level: GameLevel<*>) : LevelEntity(level, Config.GRID_CELL_SIZE.toFloat()) {

    open var speed = 0.003f
    var speedMul = 1f
    open val baseHealth = 10
    var health = 10
    open val baseDamage = 1f
    var damage = 0f
    var avoidOtherMobs = true

    val onDeath = signal1v<Mob>()
    var lastHitAngle: Angle = Angle.ZERO

    override fun update(dt: Duration) {
        super.update(dt)
        if (isCollidingWithInnerCircle(hero) && !cd.has("damageCooldown")) {
            hero.health -= damage
            cd.timeout("damageCooldown", 100.milliseconds)
        }
        if (cd.has("hit")) {
            val hitRatio = 1f - cd.ratio("hit")
            sprite.color.r = 1f
            sprite.color.g = hitRatio
            sprite.color.b = hitRatio
        }


        val camera = hero.camera.camera
        if (camera != null) {
            val distFromBounds = 100f
            if (!camera.boundsInFrustum(
                    px + distFromBounds * 0.5f,
                    py + distFromBounds * 0.5f,
                    distFromBounds,
                    distFromBounds
                )
            ) {
                teleportToRandomSpotAroundHero()
            }
        }
        if (health <= 0) {
            die()
        }

    }

    open fun hit(damage: Int, from: Angle) {
        health -= damage
        lastHitAngle = from
        sprite.color.r = 1f
        sprite.color.g = 0f
        sprite.color.b = 0f
        cd.timeout("hit", 250.milliseconds)
    }

    fun spawn() {
        enabled = true
        ALL += this
    }

    fun die(spawnDrop: Boolean = true) {
        if (spawnDrop) {
            onSpawnDrop()
        }
        onDeath.emit(this)
        enabled = false
        ALL -= this
        velocityX = 0f
        velocityY = 0f
        reset()
    }

    open fun onSpawnDrop() {
        // TODO
    }

    fun teleportToRandomSpotAroundHero() {
        val camera = hero.camera.camera ?: return
        val vw = camera.virtualWidth
        val vh = camera.virtualHeight
        val vx = camera.position.x - vw * 0.5f - Config.GRID_CELL_SIZE * 3
        val vy = camera.position.y - vh * 0.5f - Config.GRID_CELL_SIZE * 3
        val vx2 = vx + vw + Config.GRID_CELL_SIZE * 3
        val vy2 = vy + vh + Config.GRID_CELL_SIZE * 3
        val w = vw + Config.GRID_CELL_SIZE * 3 * 2
        val h = vh + Config.GRID_CELL_SIZE * 3 * 2
        val perimeter = (w * 2) + (h * 2)
        val point = Random.nextFloat() * perimeter

        var rx: Float
        var ry: Float

        if (point <= w) { // anywhere at top
            rx = (Random.nextFloat() * w) + vx
            ry = vy
        } else if (point <= w + h) { // anywhere on right
            rx = vx2
            ry = (Random.nextFloat() * h) + vy
        } else if (point <= (w * 2) + h) { // anywhere on bottom
            rx = (Random.nextFloat() * w) + vx
            ry = vy2
        } else { // anywhere on left
            rx = vx
            ry = (Random.nextFloat() * h) + vy
        }
        rx /= Config.GRID_CELL_SIZE
        ry /= Config.GRID_CELL_SIZE

        toGridPosition(ceil(rx).toInt(), ceil(ry).toInt())
    }

    open fun reset() {
        health = baseHealth
        damage = baseDamage
        globalScaleX = 1f
        globalScaleY = 1f
    }

    override fun onDestroy() {
        super.onDestroy()
        ALL -= this
    }


    companion object {
        val ALL = mutableListOf<Mob>()
    }
}