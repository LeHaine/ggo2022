package com.lehaine.game.node.entity.mob

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.Level
import com.lehaine.game.node.entity.Hero
import com.lehaine.littlekt.math.geom.Angle
import com.lehaine.littlekt.util.signal1v
import com.lehaine.rune.engine.node.renderable.entity.LevelEntity
import com.lehaine.rune.engine.node.renderable.entity.cd
import com.lehaine.rune.engine.node.renderable.entity.toGridPosition
import com.lehaine.rune.engine.node.renderable.sprite
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * @author Colton Daily
 * @date 11/4/2022
 */
open class Mob(val hero: Hero, override val level: Level) : LevelEntity(level, Config.GRID_CELL_SIZE.toFloat()) {

    open var speed = 0.003f
    var speedMul = 1f
    open val baseHealth = 10
    var health = 10
    open val baseDamage = 1f
    var damage = 0f
    var avoidOtherMobs = true

    val onDeath = signal1v<Mob>()
    var lastHitAngle: Angle = Angle.ZERO

    protected val shadow = sprite {
        name = "Shadow"
        slice = Assets.atlas.getByPrefix("shadow").slice
        x -= Config.GRID_CELL_SIZE * 0.5f
        y -= Config.GRID_CELL_SIZE  - 2f
    }.also { moveChild(it, 0) }

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
        onSpawn()
    }

    open fun onSpawn() = Unit

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
        var scanning = true
        while (scanning) {
            val fx = (0..level.levelWidth).random()
            val fy = (0..level.levelHeight).random()

            if (level.isValid(fx, fy) && !level.hasCollision(fx, fy)) {
                toGridPosition(fx, fy)
                scanning = false
            }
        }
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