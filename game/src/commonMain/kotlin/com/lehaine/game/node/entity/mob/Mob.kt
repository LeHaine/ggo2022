package com.lehaine.game.node.entity.mob

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.Level
import com.lehaine.game.node.entity.Effectible
import com.lehaine.game.node.entity.Hero
import com.lehaine.game.node.entity.SoulItem
import com.lehaine.game.node.game
import com.lehaine.littlekt.math.geom.Angle
import com.lehaine.littlekt.util.signal1v
import com.lehaine.rune.engine.node.renderable.entity.*
import com.lehaine.rune.engine.node.renderable.sprite
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * @author Colton Daily
 * @date 11/4/2022
 */
abstract class Mob(val hero: Hero, override val level: Level) : ObliqueEntity(level, Config.GRID_CELL_SIZE.toFloat()),
    Effectible {

    open var minSoulsDrop = 0
    open var maxSoulsDrop = 10

    open var speed = 0.003f
    var speedMul = 1f
    open val baseHealth = 10
    var health = 10
    var avoidOtherMobs = true

    val onDeath = signal1v<Mob>()
    var lastHitAngle: Angle = Angle.ZERO

    override val effects: MutableMap<Effect, Duration> = mutableMapOf()
    override val effectsToRemove: MutableList<Effect> = mutableListOf()

    protected val shadow = sprite {
        name = "Shadow"
        slice = Assets.atlas.getByPrefix("shadow").slice
        x -= Config.GRID_CELL_SIZE * 0.5f
        y -= Config.GRID_CELL_SIZE - 2f
    }.also { moveChild(it, 0) }

    init {
        onReady += {
            health = (baseHealth * game.state.monsterHealthMultiplier).toInt()
        }
    }

    override fun update(dt: Duration) {
        super.update(dt)
        if (isCollidingWithInnerCircle(hero) && !cd.has("damageCooldown")) {
            hero.hit(angleTo(hero))
            cd.timeout("damageCooldown", 100.milliseconds)
        }
        if (cd.has("hit")) {
            val hitRatio = 1f - cd.ratio("hit")
            sprite.color.r = 1f
            sprite.color.g = hitRatio
            sprite.color.b = hitRatio
        }

        updateEffects(dt)

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

        shadow.globalY = (cy + yr) * Config.GRID_CELL_SIZE - Config.GRID_CELL_SIZE + 2
    }

    open fun hit(from: Angle) {
        if (hasEffect(Effect.Invincible) || health <= 0) return

        health -= 1 + game.state.extraHeroDamage
        lastHitAngle = from
        sprite.color.r = 1f
        sprite.color.g = 0f
        sprite.color.b = 0f
        stretchY = 1.25f
        if (health > 0) {
            Assets.sfxHits.random().play(0.25f)
        }
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
            spawnDrop()
            Assets.sfxDeathMob.play(0.2f)
        }
        onDeath.emit(this)
        enabled = false
        ALL -= this
        velocityX = 0f
        velocityY = 0f

        reset()
    }

    override fun onLand() {
        super.onLand()
        if (!cd.has("landed")) {
            Assets.sfxLands.random().play(0.2f)
            cd("landed", 1000.milliseconds)
        }
    }

    abstract fun explode()
    protected fun spawnDrop() {
        val multiplier = game.state.soulItemDropMultiplier
        val totalDropped = ((minSoulsDrop..maxSoulsDrop).random() * multiplier).toInt()
        repeat(totalDropped) {
            SoulItem.pool.alloc().spawn(globalX, globalY)
        }
    }

    fun teleportToRandomSpotAroundHero() {
        var scanning = true
        while (scanning) {
            val fx = (0..level.levelWidth).random()
            val fy = (0..level.levelHeight).random()

            if (level.isValid(fx, fy) && !level.hasCollision(fx, fy) && hero.distGridTo(fx, fy) >= 3) {
                toGridPosition(fx, fy)
                scanning = false
            }
        }
    }

    abstract fun handleHandOfDeath()

    override fun isEffectible(): Boolean = health > 0

    open fun reset() {
        health = (baseHealth * game.state.monsterHealthMultiplier).toInt()
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