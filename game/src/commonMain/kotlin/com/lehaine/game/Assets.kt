package com.lehaine.game

import com.lehaine.littlekt.AssetProvider
import com.lehaine.littlekt.BitmapFontAssetParameter
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.Disposable
import com.lehaine.littlekt.graphics.Animation
import com.lehaine.littlekt.graphics.TextureAtlas
import com.lehaine.littlekt.graphics.TextureSlice
import com.lehaine.littlekt.graphics.font.BitmapFont
import com.lehaine.littlekt.graphics.getAnimation
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

    private val heroIdle by assets.prepare { atlas.getAnimation("heroIdle") }
    private val heroWalk by assets.prepare { atlas.getAnimation("heroWalk") }
    private val heroAttack by assets.prepare { atlas.getAnimation("heroAttack") }
    private val heroSoar by assets.prepare { atlas.getAnimation("heroSoar") }

    private val swipeAttack1 by assets.prepare { atlas.getAnimation("swipeAttack1", 75.milliseconds) }

    private val meatballStandUp by assets.prepare { atlas.getAnimation("meatBallStandUp") }
    private val meatballRun by assets.prepare { atlas.getAnimation("meatBallRun") }

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

        val heroIdle: Animation<TextureSlice> get() = INSTANCE.heroIdle
        val heroWalk: Animation<TextureSlice> get() = INSTANCE.heroWalk
        val heroAttack: Animation<TextureSlice> get() = INSTANCE.heroAttack
        val heroSoar: Animation<TextureSlice> get() = INSTANCE.heroSoar

        val swipeAttack1: Animation<TextureSlice> get() = INSTANCE.swipeAttack1

        val meatBallStandUp: Animation<TextureSlice> get() = INSTANCE.meatballStandUp
        val meatBallRun: Animation<TextureSlice> get() = INSTANCE.meatballRun

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