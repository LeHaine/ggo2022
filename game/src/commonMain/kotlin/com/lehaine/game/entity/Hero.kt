package com.lehaine.game.entity

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.GameInput
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.addTo
import com.lehaine.littlekt.graphics.tilemap.ldtk.LDtkEntity
import com.lehaine.rune.engine.GameLevel
import com.lehaine.rune.engine.node.renderable.entity.LevelEntity
import com.lehaine.rune.engine.node.renderable.entity.toGridPosition
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.sign
import kotlin.time.Duration

@OptIn(ExperimentalContracts::class)
fun Node.hero(
    data: LDtkEntity,
    level: GameLevel<*>,
    callback: Hero.() -> Unit = {}
): Hero {
    contract { callsInPlace(callback, InvocationKind.EXACTLY_ONCE) }
    return Hero(data, level).also(callback).addTo(this)
}


/**
 * @author Colton Daily
 * @date 11/2/2022
 */
class Hero(data: LDtkEntity, level: GameLevel<*>) : LevelEntity(level, Config.GRID_CELL_SIZE.toFloat()) {

    var speed = 0.03f

    private var xMoveStrength = 0f
    private var yMoveStrength = 0f


    init {
        topCollisionRatio = 0.1f
        anchorX = data.pivotX
        anchorY = data.pivotY
        toGridPosition(data.cx, data.cy)
        registerState(Assets.heroIdle, 0)
    }


    override fun update(dt: Duration) {
        super.update(dt)

        val movement = controller.vector(GameInput.MOVEMENT)
        xMoveStrength = movement.x
        yMoveStrength = movement.y
        val sign = movement.x.sign.toInt()
        dir = if (sign != 0) sign else dir
    }

    override fun fixedUpdate() {
        super.fixedUpdate()
        velocityX += speed * xMoveStrength
        velocityY += speed * yMoveStrength
    }
}