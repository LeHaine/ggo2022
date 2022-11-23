package com.lehaine.game.node.entity.mob

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.Level
import com.lehaine.game.node.entity.Effectible
import com.lehaine.game.node.entity.Hero
import com.lehaine.game.node.entity.SoulItem
import com.lehaine.game.node.fx
import com.lehaine.game.node.game
import com.lehaine.littlekt.math.geom.Angle
import com.lehaine.littlekt.math.geom.cosine
import com.lehaine.littlekt.math.geom.sine
import com.lehaine.littlekt.math.isFuzzyZero
import com.lehaine.littlekt.util.fastForEach
import com.lehaine.littlekt.util.signal1v
import com.lehaine.rune.engine.node.renderable.entity.*
import com.lehaine.rune.engine.node.renderable.sprite
import kotlin.math.ceil
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * @author Colton Daily
 * @date 11/4/2022
 */
abstract class Mob(val hero: Hero, override val level: Level) : ObliqueEntity(level, Config.GRID_CELL_SIZE.toFloat()),
    Effectible {

    open var minSoulsDrop = 1
    open var maxSoulsDrop = 2

    open var speed = 0.003f

    private var lastHealthMulitplier = 1f
    val speedMul get() = game.state.monsterSpeedMultiplier
    open val baseHealth = 2
    var health = 2
    var avoidOtherMobs = true

    val onDeath = signal1v<Mob>()
    var lastHitAngle: Angle = Angle.ZERO

    var marked = false

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
        if (lastHealthMulitplier != game.state.monsterHealthMultiplier) {
            val prevMaxHealth = (baseHealth * lastHealthMulitplier).toInt()
            lastHealthMulitplier = game.state.monsterHealthMultiplier
            val newMaxHealth = (baseHealth * game.state.monsterHealthMultiplier).toInt()
            if (health == prevMaxHealth) {
                health = newMaxHealth
            }
            if (health > newMaxHealth) {
                health = newMaxHealth
            }
        }
        if (velocityZ.isFuzzyZero(0.008f) && hero.velocityZ.isFuzzyZero(0.008f) && isCollidingWithInnerCircle(hero) && !cd.has(
                "damageCooldown"
            )
        ) {
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

    override fun fixedUpdate() {
        super.fixedUpdate()
        if (avoidOtherMobs) {
            ALL.fastForEach { mob ->
                if (mob == this) return@fastForEach
                if (isCollidingWithOuterCircle(mob)) {
                    val angle = angleTo(mob)
                    velocityX -= speed * speedMul * angle.cosine
                    velocityY -= speed * speedMul * angle.sine
                }
            }
        }
    }

    open fun hit(from: Angle) {
        if (hasEffect(Effect.Invincible) || health <= 0) return
        fx.blood(globalX, globalY, from.cosine, from.sine)
        health -= 1 + game.state.extraHeroDamage
        lastHitAngle = from
        sprite.color.r = 1f
        sprite.color.g = 0f
        sprite.color.b = 0f
        stretchY = 1.25f
        if (health > 0) {
            Assets.sfxHits.random().play(0.25f * Config.sfxMultiplier)
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
            Assets.sfxDeathMob.play(0.2f * Config.sfxMultiplier)
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
            Assets.sfxLands.random().play(0.2f * Config.sfxMultiplier)
            cd("landed", 1000.milliseconds)
        }
    }

    abstract fun explode()
    protected fun spawnDrop() {
        val multiplier = game.state.soulItemDropMultiplier
        val totalDropped = ((minSoulsDrop..maxSoulsDrop).random() * multiplier).toInt()

        SoulItem.MARKED.fastForEach {
            if (it.distPxTo(globalX, globalY) <= 48f) {
                it.combine(totalDropped)
                return
            }
        }
        repeat(totalDropped) {
            SoulItem.pool.alloc().spawn(globalX, globalY)
        }
    }

    fun teleportToRandomSpotAroundHero() {
        val camera = hero.camera.camera ?: return
        val vw = camera.virtualWidth
        val vh = camera.virtualHeight
        val vx = camera.position.x - vw * 0.5f - Config.GRID_CELL_SIZE * 3
        val vy = camera.position.y - vh * 0.5f - Config.GRID_CELL_SIZE * 3
        val vx2 = vx + vw - hero.camera.offset.x * 2 + Config.GRID_CELL_SIZE * 3
        val vy2 = vy + vh - hero.camera.offset.y * 2 + Config.GRID_CELL_SIZE * 3
        val w = vw + Config.GRID_CELL_SIZE * 3 * 2
        val h = vh + Config.GRID_CELL_SIZE * 3 * 2
        val perimeter = (w * 2) + (h * 2)

        var scanning = true
        while (scanning) {
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

            val tx = ceil(rx).toInt()
            val ty = ceil(ry).toInt()
            if (level.isValid(tx, ty) && !level.hasCollision(tx, ty)) {
                toGridPosition(tx, ty)
                scanning = false
            }
        }
    }


    fun handleHandOfDeath() {
        marked = true
        onHandOfDeath()
    }

    abstract fun onHandOfDeath()

    override fun isEffectible(): Boolean = health > 0

    open fun reset() {
        health = (baseHealth * game.state.monsterHealthMultiplier).toInt()
        globalScaleX = 1f
        globalScaleY = 1f
        marked = false
    }

    override fun onDestroy() {
        super.onDestroy()
        ALL -= this
    }

    companion object {
        val ALL = mutableListOf<Mob>()
    }
}