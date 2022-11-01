package com.lehaine.game

import com.lehaine.game.scene.GameScene
import com.lehaine.littlekt.graph.node.render.BlendMode
import com.lehaine.littlekt.graph.node.render.Material
import com.lehaine.littlekt.graphics.Color
import com.lehaine.littlekt.graphics.ParticleSimulator
import com.lehaine.littlekt.graphics.TextureSlice
import com.lehaine.littlekt.graphics.toFloatBits
import com.lehaine.littlekt.math.random
import com.lehaine.littlekt.util.seconds
import com.lehaine.rune.engine.node.renderable.ParticleBatch
import kotlin.time.Duration


class Fx(val gameScene: GameScene) {
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
        gameScene.fxBackground.apply {
            addChild(bgAdd)
            addChild(bgNormal)
        }
        gameScene.fxForeground.apply {
            addChild(topNormal)
            addChild(topAdd)
        }
    }


    fun update(dt: Duration, tmod: Float = -1f) {
        particleSimulator.update(dt, tmod)
    }

    fun runDust(x: Float, y: Float, dir: Int) {
        create(5) {
            val p = allocTopNormal(Assets.atlas.getByPrefix("fxSmallCircle").slice, x, y)
            p.scale((0.15f..0.25f).random())
            p.color.set(DUST_COLOR).also { p.colorBits = DUST_COLOR_BITS }
            p.xDelta = (0.25f..0.75f).random() * dir
            p.yDelta = -(0.05f..0.15f).random()
            p.life = (0.05f..0.15f).random().seconds
            p.scaleDelta = (0.005f..0.015f).random()
        }
    }

    private fun allocTopNormal(slice: TextureSlice, x: Float, y: Float) =
        particleSimulator.alloc(slice, x, y).also { topNormal.add(it) }

    private fun allocTopAdd(slice: TextureSlice, x: Float, y: Float) =
        particleSimulator.alloc(slice, x, y).also { topAdd.add(it) }

    private fun allocBogNormal(slice: TextureSlice, x: Float, y: Float) =
        particleSimulator.alloc(slice, x, y).also { bgNormal.add(it) }

    private fun allocBogAdd(slice: TextureSlice, x: Float, y: Float) =
        particleSimulator.alloc(slice, x, y).also { bgAdd.add(it) }

    private fun create(num: Int, createParticle: (index: Int) -> Unit) {
        for (i in 0 until num) {
            createParticle(i)
        }
    }

    companion object {
        private val DUST_COLOR = Color.fromHex("#efddc0")
        private val DUST_COLOR_BITS = DUST_COLOR.toFloatBits()
    }
}