package com.lehaine.game.node.entity

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.GameInput
import com.lehaine.game.node.controller
import com.lehaine.game.node.entity.mob.Effect
import com.lehaine.game.node.entity.mob.Mob
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.addTo
import com.lehaine.littlekt.graph.node.annotation.SceneGraphDslMarker
import com.lehaine.littlekt.graph.node.node2d.Node2D
import com.lehaine.littlekt.graphics.tilemap.ldtk.LDtkEntity
import com.lehaine.littlekt.math.geom.cosine
import com.lehaine.littlekt.math.geom.sine
import com.lehaine.littlekt.math.isFuzzyZero
import com.lehaine.littlekt.util.datastructure.Pool
import com.lehaine.littlekt.util.fastForEach
import com.lehaine.rune.engine.GameLevel
import com.lehaine.rune.engine.node.EntityCamera2D
import com.lehaine.rune.engine.node.renderable.entity.*
import com.lehaine.rune.engine.node.renderable.sprite
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalContracts::class)
fun Node.hero(
    data: LDtkEntity,
    level: GameLevel<*>,
    camera: EntityCamera2D,
    projectiles: Node2D,
    callback: @SceneGraphDslMarker Hero.() -> Unit = {}
): Hero {
    contract { callsInPlace(callback, InvocationKind.EXACTLY_ONCE) }
    return Hero(data, level, camera, projectiles).also(callback).addTo(this)
}


/**
 * @author Colton Daily
 * @date 11/2/2022
 */
class Hero(data: LDtkEntity, level: GameLevel<*>, val camera: EntityCamera2D, projectiles: Node2D) :
    ObliqueEntity(level, Config.GRID_CELL_SIZE.toFloat()), Effectible {

    val damage = 5
    private var health = 10f
    private var swipeAttackCombo = 0

    private val swipeProjectilePool: Pool<SwipeProjectile> by lazy {
        Pool(5) {
            SwipeProjectile(this).apply { enabled = false }.addTo(projectiles)
        }
    }

    private val boneSpearProjectile: Pool<BoneSpearProjectile> by lazy {
        Pool(1) {
            BoneSpearProjectile(this).apply { enabled = false }.addTo(projectiles)
        }
    }
    private val canMove = data.field<Boolean>("canMove").value

    override val effects: MutableMap<Effect, Duration> = mutableMapOf()
    override val effectsToRemove: MutableList<Effect> = mutableListOf()

    private val mobsTemp = mutableListOf<Mob>()

    private var speed = 0.03f
    private var speedMultiplier = 1f

    private var xMoveStrength = 0f
    private var yMoveStrength = 0f

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
            registerState(Assets.heroSoar, 15) { cd.has("soar") }
            registerState(Assets.heroWalk, 5) {
                !velocityX.isFuzzyZero(0.05f) || !velocityY.isFuzzyZero(
                    0.05f
                )
            }
            registerState(Assets.heroIdle, 0)
        }

        if (!canMove) {
            addEffect(Effect.Invincible, Duration.INFINITE)
        }
    }

    override fun update(dt: Duration) {
        super.update(dt)

        updateEffects(dt)
        if (cd.has("soar") || !canMove) return

        xMoveStrength = 0f
        yMoveStrength = 0f

        if (!hasEffect(Effect.Stun)) {
            val movement = controller.vector(GameInput.MOVEMENT)
            xMoveStrength = movement.x
            yMoveStrength = movement.y
            dir = dirToMouse
        }


        if (controller.down(GameInput.ATTACK)) {
            if (!cd.has("swipeAttackCD")) {
                when (swipeAttackCombo) {
                    0, 1 -> {
                        cd("swipeAttackCD", 1000.milliseconds) {
                            swipeAttackCombo++
                        }
                        swipeAttack()
                    }

                    2 -> {
                        cd("swipeAttackCD", 1000.milliseconds) {
                            swipeAttackCombo++
                        }
                        swipeAttack()
                    }

                    3 -> {
                        cd("swipeAttackCD", 1500.milliseconds) {
                            swipeAttackCombo = 0
                        }
                        swipeAttack()
                    }
                }

                cd("swipeCombo", 1300.milliseconds) {
                    swipeAttackCombo = 0
                }
            }

        } else if (controller.pressed(GameInput.SOAR) && !cd.has("soarAttack") && !cd.has("soar")) {
            val angle = angleToMouse
            xMoveStrength = angle.cosine
            yMoveStrength = angle.sine
            speedMultiplier = 5f
            sprite.color.a = 0.5f
            scaleX = 1.25f
            scaleY = 0.9f
            cd("soarAttack", 2.seconds)
            addEffect(Effect.Invincible, 350.milliseconds)
            cd("soar", 250.milliseconds) {
                speedMultiplier = 1f
                scaleX = 1f
                scaleY = 1f
            }
        } else if (controller.pressed(GameInput.HAND_OF_DEATH)) {
            performHandOfDeath()
        } else if (controller.pressed(GameInput.BONE_SPEAR)) {
            val tcx = (mouseX / Config.GRID_CELL_SIZE).toInt()
            val tcy = (mouseY / Config.GRID_CELL_SIZE).toInt()
            if (castRayTo(tcx, tcy) { cx, cy -> !level.hasCollision(cx, cy) }) {
                boneSpearAttack(mouseX, mouseY)
            }
        }
    }

    override fun onEffectStart(effect: Effect) {
        super.onEffectStart(effect)
        if (effect == Effect.Invincible) {
            sprite.color.a = 0.5f
        }
    }

    override fun onEffectEnd(effect: Effect) {
        super.onEffectEnd(effect)
        if (effect == Effect.Invincible) {
            sprite.color.a = 1f
        }
    }

    override fun fixedUpdate() {
        super.fixedUpdate()
        velocityX += speed * speedMultiplier * xMoveStrength
        velocityY += speed * speedMultiplier * yMoveStrength
    }

    fun hit(damage: Float) {
        if (hasEffect(Effect.Invincible)) return
        health -= damage
    }

    fun swipeAttack() {
        val projectile = swipeProjectilePool.alloc()
        val offset = 20f

        val angle = angleToMouse
        projectile.sprite.flipY = dir == -1
        projectile.rotation = angle
        projectile.globalPosition(globalX + offset * angle.cosine, globalY + offset * angle.sine)
        projectile.enabled = true
        addEffect(Effect.Stun, 250.milliseconds)
        velocityX += 0.1f * angleToMouse.cosine
        velocityY += 0.1f * angleToMouse.sine
    }

    fun boneSpearAttack(tx: Float, ty: Float) {
        val projectile = boneSpearProjectile.alloc()
        projectile.globalX = tx
        projectile.globalY = ty
        projectile.enabled = true
    }


    fun performHandOfDeath() {
        if (Mob.ALL.isEmpty()) return

        repeat(5) {
            var mob = Mob.ALL.random()
            while (mobsTemp.contains(mob)) {
                mob = Mob.ALL.random()
            }
            mobsTemp += mob
        }

        mobsTemp.fastForEach {
            it.handleHandOfDeath()
        }
        mobsTemp.clear()
    }


    fun projectileFinished(projectile: Projectile) {
        when (projectile) {
            is SwipeProjectile -> swipeProjectilePool.free(projectile)
            is BoneSpearProjectile -> boneSpearProjectile.free(projectile)
        }
    }

    override fun isEffectible(): Boolean = health > 0
}