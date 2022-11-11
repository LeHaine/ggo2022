package com.lehaine.game.node.entity

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.Level
import com.lehaine.game.node.game
import com.lehaine.littlekt.graph.node.addTo
import com.lehaine.littlekt.graph.node.node2d.Node2D
import com.lehaine.littlekt.math.geom.cosine
import com.lehaine.littlekt.math.geom.sine
import com.lehaine.littlekt.math.random
import com.lehaine.littlekt.util.datastructure.Pool
import com.lehaine.rune.engine.node.renderable.entity.*
import com.lehaine.rune.engine.node.renderable.sprite
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * @author Colton Daily
 * @date 11/10/2022
 */
class SoulCollectible(level: Level) : ObliqueEntity(level, Config.GRID_CELL_SIZE.toFloat()) {
    private val pickUpRange: Float = 5f
    private val speed = 0.075f
    private var xMoveStrength = 0f
    private var yMoveStrength = 0f

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

        if (cd.has("delay")) return

        xMoveStrength = 0f
        yMoveStrength = 0f

        if (distGridTo(game.hero) < pickUpRange) {
            val angle = angleTo(game.hero)
            xMoveStrength = angle.cosine
            yMoveStrength = angle.sine

            if (distPxTo(game.hero) < 8f) {
                game.state.soulsCaptured++
                xMoveStrength = 0f
                yMoveStrength = 0f
                enabled = false
                pool.free(this)
            }
        }

        shadow.globalY = (cy + yr) * Config.GRID_CELL_SIZE
    }

    override fun fixedUpdate() {
        super.fixedUpdate()
        velocityX += speed * xMoveStrength
        velocityY += speed * yMoveStrength
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
    }

    companion object {
        private var _pool: Pool<SoulCollectible>? = null
        val pool get() = _pool!!

        fun initPool(level: Level, entities: Node2D) {
            _pool = Pool(100) {
                SoulCollectible(level).apply { enabled = false }.addTo(entities)
            }
        }
    }
}