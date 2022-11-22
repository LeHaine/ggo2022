package com.lehaine.game.node.entity

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.Level
import com.lehaine.game.node.game
import com.lehaine.littlekt.graph.node.addTo
import com.lehaine.littlekt.graph.node.node2d.Node2D
import com.lehaine.littlekt.math.distSqr
import com.lehaine.littlekt.math.geom.cosine
import com.lehaine.littlekt.math.geom.sine
import com.lehaine.littlekt.math.random
import com.lehaine.littlekt.util.datastructure.Pool
import com.lehaine.littlekt.util.fastForEach
import com.lehaine.rune.engine.node.renderable.entity.*
import com.lehaine.rune.engine.node.renderable.sprite
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * @author Colton Daily
 * @date 11/10/2022
 */
class SoulItem(level: Level) : ObliqueEntity(level, Config.GRID_CELL_SIZE.toFloat()) {
    private val pickUpRange: Float = 5f
    private val speed = 0.075f
    private var xMoveStrength = 0f
    private var yMoveStrength = 0f
    private var target: SoulItem? = null
    private var marked = false
    private var exp = 1

    private val shadow = sprite {
        name = "Shadow"
        slice = Assets.atlas.getByPrefix("shadowXSmall").slice
        x -= slice?.width?.times(0.5f) ?: 0f
    }.also { moveChild(it, 0) }

    init {
        sprite.slice = Assets.atlas.getByPrefix("soulBlob").slice
    }

    override fun update(dt: Duration) {
        super.update(dt)

        shadow.globalY = (cy + yr) * Config.GRID_CELL_SIZE

        if (cd.has("delay")) return

        xMoveStrength = 0f
        yMoveStrength = 0f

        if (!cd.has("combine") && target == null && !marked) {
            run combine@{
                MARKED.fastForEach {
                    val dist = outerRadius * 3f + it.outerRadius * 3f
                    val withinRange = distSqr(centerX, centerY, it.centerX, it.centerY) <= dist * dist
                    if (withinRange && it.marked) {
                        target = it
                        return@combine
                    }
                }
                if (target == null) {
                    marked = true
                    MARKED += this
                }
            }
        }
        if (distGridTo(game.hero) < pickUpRange) {
            val angle = angleTo(game.hero)
            xMoveStrength = angle.cosine
            yMoveStrength = angle.sine

            if (distPxTo(game.hero) < 8f) {
                game.state.soulsCaptured += exp
                game.state.exp.add(exp)
                ALL -= this
                Assets.sfxCollect.play((0.1f..0.2f).random() * Config.sfxMultiplier)
                xMoveStrength = 0f
                yMoveStrength = 0f
                scaleX = 1f
                scaleY = 1f
                enabled = false
                target = null
                marked = false
                MARKED -= this
                pool.free(this)
            }
        } else {
            val target = target ?: return
            val angle = angleTo(target)
            xMoveStrength = angle.cosine
            yMoveStrength = angle.sine

            if (distPxTo(target) < 8f) {
                target.combine(exp)
                ALL -= this
                xMoveStrength = 0f
                yMoveStrength = 0f
                enabled = false
                this.target = null
                marked = false
                pool.free(this)
            }
        }
    }

    override fun fixedUpdate() {
        super.fixedUpdate()
        velocityX += speed * xMoveStrength
        velocityY += speed * yMoveStrength
    }

    fun combine(add: Int) {
        exp += add
        scaleX = (scaleX + 0.01f * add).coerceAtMost(2f)
        scaleY = (scaleY + 0.01f * add).coerceAtMost(2f)
    }

    override fun onLand() {
        super.onLand()
        if (velocityZ <= 0.07f) {
            velocityZ = 0f
        }
    }

    fun spawn(tx: Float, ty: Float) {
        enabled = true
        cd("delay", 600.milliseconds)
        globalPosition(tx, ty)
        velocityX = (-0.3f..0.3f).random()
        velocityY = (-0.3f..0.3f).random()
        velocityZ = (0.05f..0.2f).random()
        ALL += this
        cd("combine", 3.seconds)

        run combine@{
            MARKED.fastForEach {
                val dist = outerRadius * 3f + it.outerRadius * 3f
                val withinRange = distSqr(centerX, centerY, it.centerX, it.centerY) <= dist * dist
                if (withinRange && it.marked) {
                    target = it
                    return@combine
                }
            }
        }
    }

    companion object {
        val MARKED = mutableListOf<SoulItem>()
        val ALL = mutableListOf<SoulItem>()
        private var _pool: Pool<SoulItem>? = null
        val pool get() = _pool!!

        fun initPool(level: Level, entities: Node2D) {
            _pool = Pool(100) {
                SoulItem(level).apply { enabled = false }.addTo(entities)
            }
        }
    }
}