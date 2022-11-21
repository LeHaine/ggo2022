package com.lehaine.game

import com.lehaine.game.scene.GameScene
import com.lehaine.littlekt.graph.node.render.BlendMode
import com.lehaine.littlekt.graph.node.render.Material
import com.lehaine.littlekt.graphics.Color
import com.lehaine.littlekt.graphics.Particle
import com.lehaine.littlekt.graphics.ParticleSimulator
import com.lehaine.littlekt.graphics.TextureSlice
import com.lehaine.littlekt.math.PI2_F
import com.lehaine.littlekt.math.geom.radians
import com.lehaine.littlekt.math.random
import com.lehaine.littlekt.util.milliseconds
import com.lehaine.littlekt.util.seconds
import com.lehaine.rune.engine.node.renderable.ParticleBatch
import kotlin.math.sign
import kotlin.random.Random
import kotlin.time.Duration


class Fx(val game: GameScene) {
    private val particleSimulator = ParticleSimulator(2048)

    private var bgAdd = ParticleBatch().apply {
        material = Material().apply {
            blendMode = BlendMode.Add
        }
    }
    private var bgNormal = ParticleBatch()
    private var topAdd = ParticleBatch().apply {
        material = Material().apply {
            blendMode = BlendMode.Add
        }
    }
    private var topNormal = ParticleBatch()

    fun createParticleBatchNodes() {
        bgAdd = ParticleBatch().apply {
            material = Material().apply {
                blendMode = BlendMode.Add
            }
        }
        bgNormal = ParticleBatch()
        topAdd = ParticleBatch().apply {
            material = Material().apply {
                blendMode = BlendMode.Add
            }
        }
        topNormal = ParticleBatch()
        game.fxBackground.apply {
            addChild(bgAdd)
            addChild(bgNormal)
        }
        game.fxForeground.apply {
            addChild(topNormal)
            addChild(topAdd)
        }
    }


    fun update(dt: Duration, tmod: Float = -1f) {
        particleSimulator.update(dt, tmod)
    }

    fun levelUp(x: Float, y: Float) {
        create(25) {
            val p = allocBotNormal(Assets.atlas.getByPrefix("fxPixel").slice, x, y)
            p.xDelta = (0f..0.7f).random().asRandomSign
            p.yDelta = -(1..2).random()
            p.life = (0.2f..0.3f).random().seconds
            p.color.set(pickOne(LIGHT_MEAT_RED, BONE_WHITE))
            p.friction = 0.97f.about(0.05f).coerceAtMost(1f)
        }
    }


    fun shadowSmall(x: Float, y: Float, duration: Duration) {
        create(1) {
            val shadowSlice = Assets.atlas.getByPrefix("shadowSmall").slice
            val p = allocBotNormal(shadowSlice, x - shadowSlice.width * 0.5f, y - shadowSlice.height * 0.5f)
            p.life = duration
        }
    }

    fun groundParticles(x: Float, y: Float) {
        create(30) {
            val p = allocTopNormal(Assets.atlas.getByPrefix("fxDot").slice, x, y)
            p.xDelta = (0..2).random().asRandomSign
            p.yDelta = -(1..2).random()
            p.color.set(pickOne(MEAT_RED, DARK_MEAT_RED, LIGHT_MEAT_RED))
            p.life = (1f..1.2f).random().seconds
            p.friction = 0.97f.about(0.05f).coerceAtMost(1f)
            p.rotationDelta = (0f..PI2_F).random()
            p.gravityY = 0.1f.about()
            p.data0 = y + (0..12).random().toInt()
            p.onUpdate = ::fleshGroundPhysics
        }
    }

    fun spiritBallExplode(x: Float, y: Float) {
        create(10) {
            val p = allocTopNormal(Assets.atlas.getByPrefix("fxDot").slice, x, y)
            p.color.set(pickOne(LIGHT_PURPLE, DARK_PURPLE, LIGHT_MEAT_RED))
            p.xDelta = (0.5f..0.7f).random().asRandomSign
            p.yDelta = (0.5f..0.7f).random().asRandomSign
            p.friction = 0.97f.about(0.05f).coerceAtMost(1f)
            p.rotationDelta = (0f..PI2_F).random()
            p.life = (0.5f..0.75f).random().seconds
        }
    }


    fun smallExplosion(x: Float, y: Float) {
        val smallCircle = Assets.atlas.getByPrefix("fxSmallCircle").slice
        val dot = Assets.atlas.getByPrefix("fxDot").slice

        // smoke
        val smoke = Assets.atlas.getByPrefix("fxSmoke").slice
        repeat(10) {
            val p = allocBotNormal(smoke, x + (0..5).random().asRandomSign, y + (0..5).random().asRandomSign)
            p.fadeOutSpeed = (0.5f..1f).random()
            p.scale((0.25f..0.5f).random())
            p.xDelta = (0f..1.3f).random().asRandomSign
            p.yDelta = (-2f..0f).random()
            p.friction = (0.93f..0.96f).random()
            p.rotation = (0f..PI2_F).random().radians
            p.rotationDelta = (0f..0.02f).random().asRandomSign
            p.life = (4f..5f).random().seconds
            p.delay = if (it > 20) (0..100).random().milliseconds else Duration.ZERO

        }

        // Fire
        repeat(10) {
            val p = allocTopAdd(smoke, x + (0..3).random().asRandomSign, y + (0..6).random().asRandomSign)
            p.color.set(FIRE)
            p.fadeOutSpeed = (0.5f..1f).random()
            p.scale((0.15f..0.35f).random())
            p.moveAwayFrom(x, y, (0f..2f).random())
            p.friction = (0.93f..0.96f).random()
            p.rotation = (0f..PI2_F).random().radians
            p.rotationDelta = (0f..0.02f).random().asRandomSign
            p.life = (1f..3f).random().seconds
            p.delay = if (it > 20) (0..100).random().milliseconds else Duration.ZERO

        }
    }

    fun chickenExplode(x: Float, y: Float) {
        fun setParticle(p: Particle) {
            p.xDelta = (0..2).random().asRandomSign
            p.yDelta = (1..2).random().asRandomSign
            p.gravityY = 0.1f.about()
            p.friction = 0.97f.about(0.05f).coerceAtMost(1f)
            p.rotationDelta = (0f..PI2_F).random()
            p.data0 = y + (0..12).random().toInt()
            p.alpha = (0.7f..1f).random()
            p.life = (1..2).random().seconds
            p.onUpdate = ::bloodPhysics
        }
        create(1) {
            val p = allocTopNormal(Assets.atlas.getByPrefix("fxChickenSpear").slice, x, y)
            setParticle(p)
        }
        create(25) {
            val idx = Random.nextInt(3)
            val p = allocTopNormal(Assets.atlas.getByPrefix("fxGib$idx").slice, x, y)
            p.color.set(PURPLE)
            setParticle(p)
        }
    }

    fun meatBallExplode(x: Float, y: Float) {
        fun setParticle(p: Particle) {
            p.xDelta = (0..2).random().asRandomSign
            p.yDelta = (1..2).random().asRandomSign
            p.gravityY = 0.1f.about()
            p.friction = 0.97f.about(0.05f).coerceAtMost(1f)
            p.rotationDelta = (0f..PI2_F).random()
            p.data0 = y + (0..12).random().toInt()
            p.alpha = (0.7f..1f).random()
            p.life = (1..2).random().seconds
            p.onUpdate = ::bloodPhysics
        }
        create(1) {
            val p = allocTopNormal(Assets.atlas.getByPrefix("fxBigEye").slice, x, y)
            setParticle(p)
        }
        create(1) {
            val p = allocTopNormal(Assets.atlas.getByPrefix("fxLittleEye").slice, x, y)
            setParticle(p)
        }
        create(2) {
            val p = allocTopNormal(Assets.atlas.getByPrefix("fxMeatLeg").slice, x, y)
            setParticle(p)
        }
        create(50) {
            val idx = Random.nextInt(3)
            val p = allocTopNormal(Assets.atlas.getByPrefix("fxGib$idx").slice, x, y)
            p.color.set(MEAT_RED)
            setParticle(p)
        }
    }

    private fun bloodPhysics(particle: Particle) {
        if (particle.isColliding() && particle.data0 != 1f) {
            particle.data0 = 1f
            particle.xDelta *= 0.4f
            particle.yDelta = 0f
            particle.gravityY = (0f..0.001f).random()
            particle.friction = (0.5f..0.7f).random()
            particle.scaleDeltaY = (0f..0.001f).random()
            particle.rotationDelta = 0f
            if (particle.isColliding(-5) || particle.isColliding(5)) {
                particle.scaleY *= (1f..1.25f).random()
            }
            if (particle.isColliding(offsetY = -5) || particle.isColliding(offsetY = 5)) {
                particle.scaleX *= (1f..1.25f).random()
            }
        }
        if (particle.y >= particle.data0 && particle.yDelta > 0) {
            particle.gravityY = 0f
            particle.yDelta = 0f
        }
    }

    private fun fleshGroundPhysics(particle: Particle) {
        if (particle.isColliding() && particle.data0 != 1f) {
            particle.data0 = 1f
            particle.xDelta *= 0.4f
            particle.yDelta = 0f
            particle.gravityY = 0f
            particle.friction = (0.5f..0.7f).random()
            particle.scaleDeltaY = (0f..0.001f).random()
            particle.rotationDelta = 0f
            if (particle.isColliding(-5) || particle.isColliding(5)) {
                particle.scaleY *= (1f..1.25f).random()
            }
            if (particle.isColliding(offsetY = -5) || particle.isColliding(offsetY = 5)) {
                particle.scaleX *= (1f..1.25f).random()
            }
        }
        if (particle.y >= particle.data0 && particle.yDelta > 0) {
            particle.gravityY = 0f
            particle.yDelta = 0f
        }
    }

    private fun groundPhysics(particle: Particle) {
        if (!particle.isColliding()) {
            if (particle.isColliding(2 * particle.xDelta.sign.toInt())) {
                particle.xDelta = -particle.xDelta * 0.7f
            }
            if (particle.isColliding(offsetY = 2 * particle.yDelta.sign.toInt())) {
                particle.yDelta = -particle.yDelta * 0.7f
            }
        }

        if (particle.isColliding() || particle.y >= particle.data0 && particle.yDelta > 0) {
            particle.data0++
            if (particle.data0 == 1f) {
                particle.gravityY = 0f
                particle.yDelta = 0f
                particle.xDelta *= 0.5f
                particle.rotationDelta = 0f
            } else {
                particle.yDelta = -particle.yDelta
                particle.xDelta *= 0.6f
                particle.rotationDelta *= 0.3f
            }
        }
    }

    private fun allocTopNormal(slice: TextureSlice, x: Float, y: Float) =
        particleSimulator.alloc(slice, x, y).also { topNormal.add(it) }

    private fun allocTopAdd(slice: TextureSlice, x: Float, y: Float) =
        particleSimulator.alloc(slice, x, y).also { topAdd.add(it) }

    private fun allocBotNormal(slice: TextureSlice, x: Float, y: Float) =
        particleSimulator.alloc(slice, x, y).also { bgNormal.add(it) }

    private fun allocBotAdd(slice: TextureSlice, x: Float, y: Float) =
        particleSimulator.alloc(slice, x, y).also { bgAdd.add(it) }

    private fun create(num: Int, createParticle: (index: Int) -> Unit) {
        for (i in 0 until num) {
            createParticle(i)
        }
    }


    private fun Particle.isColliding(offsetX: Int = 0, offsetY: Int = 0) =
        game.level.hasCollision(
            ((x + offsetX) / Config.GRID_CELL_SIZE).toInt(),
            ((y + offsetY) / Config.GRID_CELL_SIZE).toInt()
        )

    private fun Float.about(variance: Float = 0.1f, sign: Boolean = false): Float {
        return this * (1 + (0..(variance * 100).toInt() / 100).random()) * (if (sign) randomSign else 1)
    }

    private fun Int.about(variance: Float = 0.1f, sign: Boolean = false): Float {
        return about(this.toFloat(), sign)
    }

    private fun <T> pickOne(one: T, two: T, three: T): T {
        val r = Random.nextInt(3)
        if (r == 0) return one
        if (r == 1) return two
        return three
    }

    private fun <T> pickOne(one: T, two: T, three: T, four: T): T {
        val r = Random.nextInt(4)
        if (r == 0) return one
        if (r == 1) return two
        if (r == 2) return three
        return four
    }

    private fun <T> pickOne(one: T, two: T): T {
        val r = Random.nextFloat()
        if (r < 0.5f) return one
        return two
    }

    private val randomSign: Int get() = (0..1).random().toInt() * 2 - 1
    private val Float.asRandomSign: Float get() = if (Random.nextFloat() >= 0.5f) this else -this
    private val Int.asRandomSign: Int get() = if (Random.nextFloat() >= 0.5f) this else -this

    companion object {
        private val MEAT_RED = Color.fromHex("#994551")
        private val LIGHT_MEAT_RED = Color.fromHex("#a25d64")
        private val DARK_MEAT_RED = Color.fromHex("#703d57")
        private val LIGHT_PURPLE = DARK_MEAT_RED
        private val BONE_WHITE = Color.fromHex("#e3d3cf")
        private val PURPLE = Color.fromHex("#573746")
        private val DARK_PURPLE = Color.fromHex("#422e37")
        private val BLACK = Color.fromHex("#332e30")
        private val FIRE = Color.fromHex("#E78F0C")
    }
}