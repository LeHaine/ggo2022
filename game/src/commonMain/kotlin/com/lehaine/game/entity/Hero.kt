package com.lehaine.game.entity

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.GameInput
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.addTo
import com.lehaine.littlekt.graph.node.node2d.Node2D
import com.lehaine.littlekt.graphics.tilemap.ldtk.LDtkEntity
import com.lehaine.littlekt.math.geom.cosine
import com.lehaine.littlekt.math.geom.sine
import com.lehaine.littlekt.math.isFuzzyZero
import com.lehaine.littlekt.util.datastructure.Pool
import com.lehaine.rune.engine.GameLevel
import com.lehaine.rune.engine.node.EntityCamera2D
import com.lehaine.rune.engine.node.renderable.entity.LevelEntity
import com.lehaine.rune.engine.node.renderable.entity.cd
import com.lehaine.rune.engine.node.renderable.entity.toGridPosition
import com.lehaine.rune.engine.node.renderable.sprite
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.sign
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalContracts::class)
fun Node.hero(
    data: LDtkEntity,
    level: GameLevel<*>,
    camera: EntityCamera2D,
    projectiles: Node2D,
    callback: Hero.() -> Unit = {}
): Hero {
    contract { callsInPlace(callback, InvocationKind.EXACTLY_ONCE) }
    return Hero(data, level, camera, projectiles).also(callback).addTo(this)
}


/**
 * @author Colton Daily
 * @date 11/2/2022
 */
class Hero(data: LDtkEntity, level: GameLevel<*>, val camera: EntityCamera2D, projectiles: Node2D) :
    LevelEntity(level, Config.GRID_CELL_SIZE.toFloat()) {

    val damange = 5
    var health = 10f

    private val swipeProjectilePool: Pool<SwipeProjectile> by lazy {
        Pool(5) {
            SwipeProjectile(this).apply { enabled = false }.addTo(projectiles)
        }
    }

    private var speed = 0.03f
    private var speedMultiplier = 1f

    private var xMoveStrength = 0f
    private var yMoveStrength = 0f
    private var attacking = false

    private var tx = 1
    private var ty = 1

    private val shadow = sprite {
        name = "Shadow"
        slice = Assets.atlas.getByPrefix("shadow").slice
        x -= Config.GRID_CELL_SIZE * data.pivotX
        y -= Config.GRID_CELL_SIZE * data.pivotY - 2f
    }


    init {
        moveChild(shadow, 0)
        anchorX = data.pivotX
        anchorY = data.pivotY
        toGridPosition(data.cx, data.cy)
        sprite.apply {
            registerState(Assets.heroSoar, 15) { attacking && tx != -1 && ty != -1 }
            registerState(Assets.heroWalk, 5) {
                !attacking && !velocityX.isFuzzyZero(0.05f) || !velocityY.isFuzzyZero(
                    0.05f
                )
            }
            registerState(Assets.heroIdle, 0)
        }
    }

    override fun update(dt: Duration) {
        super.update(dt)

        if (!attacking) {
            val movement = controller.vector(GameInput.MOVEMENT)
            xMoveStrength = movement.x
            yMoveStrength = movement.y
            dir = dirToMouse
        }

        if (controller.pressed(GameInput.ATTACK) && !cd.has("swipeAttack")) {
            cd("swipeAttack", 250.milliseconds)
            swipeAttack()
        }
    }

    override fun fixedUpdate() {
        super.fixedUpdate()
        velocityX += speed * speedMultiplier * xMoveStrength
        velocityY += speed * speedMultiplier * yMoveStrength
    }

    fun swipeAttack() {
        val projectile = swipeProjectilePool.alloc()
        val offset = 20f

        val angle = angleToMouse
        projectile.rotation = angle
        projectile.globalPosition(globalX + offset * angle.cosine, globalY + offset * angle.sine)
        projectile.enabled = true
    }

    fun projectileFinished(projectile: Projectile) {
        when (projectile) {
            is SwipeProjectile -> {
                swipeProjectilePool.free(projectile)
            }
        }
    }
}