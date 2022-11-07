package com.lehaine.game

import com.lehaine.littlekt.AssetProvider
import com.lehaine.littlekt.BitmapFontAssetParameter
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.Disposable
import com.lehaine.littlekt.graphics.*
import com.lehaine.littlekt.graphics.font.BitmapFont
import kotlin.jvm.Volatile
import kotlin.time.Duration.Companion.milliseconds

class Assets private constructor(context: Context) : Disposable {
    private val assets = AssetProvider(context)
    private val atlas: TextureAtlas by assets.load(context.resourcesVfs["tiles.atlas.json"])
    private val pixelFont: BitmapFont by assets.prepare {
        assets.loadSuspending<BitmapFont>(
            context.resourcesVfs["m5x7_16_outline.fnt"],
            BitmapFontAssetParameter(preloadedTextures = listOf(atlas["m5x7_16_outline_0"].slice))
        ).content
    }

    private val boneManIdle by assets.prepare { atlas.getAnimation("boneManIdle", 250.milliseconds) }

    private val heroIdle by assets.prepare { atlas.getAnimation("heroIdle") }
    private val heroWalk by assets.prepare { atlas.getAnimation("heroWalk") }
    private val heroAttack by assets.prepare { atlas.getAnimation("heroAttack") }
    private val heroSoar by assets.prepare { atlas.getAnimation("heroSoar") }

    private val swipeAttack1 by assets.prepare { atlas.getAnimation("swipeAttack1", 75.milliseconds) }
    private val boneSpearAttack by assets.prepare { atlas.getAnimation("boneSpearAttack", 75.milliseconds) }

    private val meatBallStandUp by assets.prepare { atlas.getAnimation("meatBallStandUp") }
    private val meatBallRun by assets.prepare { atlas.getAnimation("meatBallRun") }
    private val meatBallSit by assets.prepare { atlas.getAnimation("meatBallSit", 250.milliseconds) }
    private val meatBallHandOfDeath by assets.prepare {
        atlas.createAnimation("meatBallHandOfDeathGrab") {
            frames(0..1)
            frames(2, frameTime = 500.milliseconds)
            frames(3, frameTime = 1000.milliseconds)
        }
    }


    override fun dispose() {
        atlas.dispose()
        pixelFont.dispose()
    }

    companion object {
        @Volatile
        private var instance: Assets? = null
        private val INSTANCE: Assets get() = instance ?: error("Instance has not been created!")

        val atlas: TextureAtlas get() = INSTANCE.atlas
        val pixelFont: BitmapFont get() = INSTANCE.pixelFont

        val boneManIdle: Animation<TextureSlice> get() = INSTANCE.boneManIdle

        val heroIdle: Animation<TextureSlice> get() = INSTANCE.heroIdle
        val heroWalk: Animation<TextureSlice> get() = INSTANCE.heroWalk
        val heroAttack: Animation<TextureSlice> get() = INSTANCE.heroAttack
        val heroSoar: Animation<TextureSlice> get() = INSTANCE.heroSoar

        val swipeAttack1: Animation<TextureSlice> get() = INSTANCE.swipeAttack1
        val boneSpearAttack: Animation<TextureSlice> get() = INSTANCE.boneSpearAttack

        val meatBallStandUp: Animation<TextureSlice> get() = INSTANCE.meatBallStandUp
        val meatBallRun: Animation<TextureSlice> get() = INSTANCE.meatBallRun
        val meatBallSit: Animation<TextureSlice> get() = INSTANCE.meatBallSit
        val meatBallHandOfDeath: Animation<TextureSlice> get() = INSTANCE.meatBallHandOfDeath


        fun createInstance(context: Context, onLoad: () -> Unit): Assets {
            check(instance == null) { "Instance already created!" }
            val newInstance = Assets(context)
            instance = newInstance
            INSTANCE.assets.onFullyLoaded = onLoad
            context.onRender { INSTANCE.assets.update() }
            return newInstance
        }

        fun dispose() {
            instance?.dispose()
        }
    }
}