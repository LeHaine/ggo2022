package com.lehaine.game

import com.lehaine.littlekt.AssetProvider
import com.lehaine.littlekt.BitmapFontAssetParameter
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.Disposable
import com.lehaine.littlekt.audio.AudioClip
import com.lehaine.littlekt.audio.AudioStream
import com.lehaine.littlekt.graph.node.component.NinePatchDrawable
import com.lehaine.littlekt.graph.node.component.Theme
import com.lehaine.littlekt.graph.node.component.createDefaultTheme
import com.lehaine.littlekt.graph.node.ui.Button
import com.lehaine.littlekt.graph.node.ui.Label
import com.lehaine.littlekt.graph.node.ui.Panel
import com.lehaine.littlekt.graph.node.ui.ProgressBar
import com.lehaine.littlekt.graphics.*
import com.lehaine.littlekt.graphics.font.BitmapFont
import kotlin.jvm.Volatile
import kotlin.time.Duration.Companion.milliseconds

class Assets private constructor(context: Context) : Disposable {
    private val assets = AssetProvider(context)
    private val atlas: TextureAtlas by assets.load(context.resourcesVfs["tiles.atlas.json"])
    private val pixelFont: BitmapFont by assets.prepare {
        assets.loadSuspending<BitmapFont>(
            context.resourcesVfs["monogramextended_16.fnt"],
            BitmapFontAssetParameter(preloadedTextures = listOf(atlas["monogramextended_16_0"].slice))
        ).content
    }
    private val pixelFontOutline: BitmapFont by assets.prepare {
        assets.loadSuspending<BitmapFont>(
            context.resourcesVfs["monogramextended_16_outline.fnt"],
            BitmapFontAssetParameter(preloadedTextures = listOf(atlas["monogramextended_16_outline_0"].slice))
        ).content
    }

    private val heartBeating by assets.prepare { atlas.getAnimation("heartBeating") }
    private val levelUp by assets.prepare { atlas.getAnimation("levelUp") }

    private val boneManIdle by assets.prepare { atlas.getAnimation("boneManIdle", 250.milliseconds) }
    private val boneManPunish by assets.prepare { atlas.getAnimation("boneManPunish", 150.milliseconds) }

    private val heroIdle by assets.prepare { atlas.getAnimation("heroIdle") }
    private val heroWalk by assets.prepare { atlas.getAnimation("heroWalk") }
    private val heroAttack by assets.prepare { atlas.getAnimation("heroAttack") }
    private val heroDash by assets.prepare { atlas.getAnimation("heroSoar") }
    private val heroSwing by assets.prepare { atlas.getAnimation("heroSwing") }
    private val heroDie by assets.prepare { atlas.getAnimation("heroDie") }
    private val heroDead by assets.prepare {
        Animation(listOf(atlas.getByPrefix("heroDie1").slice), listOf(0), listOf(100.milliseconds))
    }
    private val heroAir by assets.prepare { atlas.getAnimation("heroAir") }

    private val swipeAttack1 by assets.prepare { atlas.getAnimation("swipeAttack1", 75.milliseconds) }
    private val swipeAttack2 by assets.prepare { atlas.getAnimation("swipeAttack2", 75.milliseconds) }
    private val swipeAttack3 by assets.prepare { atlas.getAnimation("swipeAttack3", 75.milliseconds) }
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

    private val chickenSpearRun by assets.prepare { atlas.getAnimation("chickenSpearRun") }
    private val chickenSpearHandOfDeath by assets.prepare {
        atlas.createAnimation("chickenSpearHandOfDeathGrab") {
            frames(0..1)
            frames(2, frameTime = 500.milliseconds)
            frames(3, frameTime = 1000.milliseconds)
        }
    }


    private val hopperManHop by assets.prepare { atlas.getAnimation("hopperManHop") }
    private val hopperManAir by assets.prepare { atlas.getAnimation("hopperManAir") }

    private val beetleRun by assets.prepare { atlas.getAnimation("beetleRun") }

    private val batFlap by assets.prepare { atlas.getAnimation("batFlap") }

    private val sfxFootstep: AudioClip by assets.load(context.resourcesVfs["sfx/footstep0.wav"])

    private val sfxHit0: AudioClip by assets.load(context.resourcesVfs["sfx/hit0.wav"])
    private val sfxHit1: AudioClip by assets.load(context.resourcesVfs["sfx/hit1.wav"])
    private val sfxHit2: AudioClip by assets.load(context.resourcesVfs["sfx/hit2.wav"])

    private val sfxHits by assets.prepare {
        listOf(
            sfxHit0,
            sfxHit1,
            sfxHit2,
        )
    }

    private val sfxLand0: AudioClip by assets.load(context.resourcesVfs["sfx/land0.wav"])
    private val sfxLand1: AudioClip by assets.load(context.resourcesVfs["sfx/land1.wav"])

    private val sfxLands by assets.prepare {
        listOf(
            sfxLand0,
            sfxLand1,
        )
    }

    private val sfxSwing0: AudioClip by assets.load(context.resourcesVfs["sfx/swing0.wav"])
    private val sfxSwing1: AudioClip by assets.load(context.resourcesVfs["sfx/swing1.wav"])
    private val sfxSwing2: AudioClip by assets.load(context.resourcesVfs["sfx/swing2.wav"])
    private val sfxSwing3: AudioClip by assets.load(context.resourcesVfs["sfx/swing3.wav"])
    private val sfxSwing4: AudioClip by assets.load(context.resourcesVfs["sfx/swing4.wav"])
    private val sfxSwing5: AudioClip by assets.load(context.resourcesVfs["sfx/swing5.wav"])
    private val sfxSwing6: AudioClip by assets.load(context.resourcesVfs["sfx/swing6.wav"])
    private val sfxSwing7: AudioClip by assets.load(context.resourcesVfs["sfx/swing7.wav"])
    private val sfxSwing8: AudioClip by assets.load(context.resourcesVfs["sfx/swing8.wav"])
    private val sfxSwing9: AudioClip by assets.load(context.resourcesVfs["sfx/swing9.wav"])

    private val sfxSwings by assets.prepare {
        listOf(
            sfxSwing0,
            sfxSwing1,
            sfxSwing2,
            sfxSwing3,
            sfxSwing4,
            sfxSwing5,
            sfxSwing6,
            sfxSwing7,
            sfxSwing8,
            sfxSwing9
        )
    }

    private val sfxCollect: AudioClip by assets.load(context.resourcesVfs["sfx/collect0.wav"])
    private val sfxShoot: AudioClip by assets.load(context.resourcesVfs["sfx/shoot0.wav"])
    private val sfxSelect: AudioClip by assets.load(context.resourcesVfs["sfx/select0.wav"])
    private val sfxSlam: AudioClip by assets.load(context.resourcesVfs["sfx/slam0.wav"])
    private val sfxSkillUnlock: AudioClip by assets.load(context.resourcesVfs["sfx/skillUnlock0.wav"])
    private val sfxDeathHero: AudioClip by assets.load(context.resourcesVfs["sfx/deathHero0.wav"])
    private val sfxDeathMob: AudioClip by assets.load(context.resourcesVfs["sfx/deathMob0.wav"])
    private val music: AudioStream by assets.load(context.resourcesVfs["sfx/music.mp3"])

    init {
        assets.prepare {
            val button9p = NinePatch(atlas.getByPrefix("uiButton").slice, 1, 1, 1, 1)
            val buttonHighlight9p = NinePatch(atlas.getByPrefix("uiButtonHighlight").slice, 1, 1, 1, 1)
            val panel9p = NinePatch(atlas.getByPrefix("uiPanel").slice, 15, 15, 15, 1)
            val outline9p = NinePatch(atlas.getByPrefix("uiOutline").slice, 1, 1, 1, 1)
            val pixel9p = NinePatch(atlas.getByPrefix("fxPixel").slice, 0, 0, 0, 0)

            val theme = createDefaultTheme(
                extraDrawables = mapOf(
                    "Button" to mapOf(
                        Button.themeVars.normal to NinePatchDrawable(button9p),
                        Button.themeVars.pressed to NinePatchDrawable(button9p).apply {
                            modulate = Color.WHITE.toMutableColor().scaleRgb(0.6f)
                        },
                        Button.themeVars.hover to NinePatchDrawable(buttonHighlight9p),
                        Button.themeVars.focus to NinePatchDrawable(outline9p),
                        Button.themeVars.disabled to NinePatchDrawable(buttonHighlight9p).apply {
                            modulate = Color.WHITE.toMutableColor().scaleRgb(0.6f)
                        }
                    ),
                    "Panel" to mapOf(
                        Panel.themeVars.panel to NinePatchDrawable(panel9p)
                    ),
                    "ProgressBar" to mapOf(
                        ProgressBar.themeVars.bg to NinePatchDrawable(pixel9p).apply {
                            modulate = Color.fromHex("#422e37")
                        },
                        ProgressBar.themeVars.fg to NinePatchDrawable(pixel9p).apply {
                            modulate = Color.fromHex("#994551")
                        }
                    ),
                ),
                extraColors = mapOf(
                    "Label" to mapOf(
                        Label.themeVars.fontColor to Color.fromHex("#f2e6e6")
                    ),
                    "Button" to mapOf(
                        Button.themeVars.fontColor to Color.fromHex("#f2e6e6")
                    ),
                    "ProgressBar" to mapOf(
                        ProgressBar.themeVars.fontColor to Color.fromHex("#f2e6e6")
                    )
                ),
                defaultFont = pixelFont
            )

            Theme.defaultTheme = theme
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
        val pixelFontOutline: BitmapFont get() = INSTANCE.pixelFontOutline

        val heartBeating: Animation<TextureSlice> get() = INSTANCE.heartBeating
        val levelUp: Animation<TextureSlice> get() = INSTANCE.levelUp

        val boneManIdle: Animation<TextureSlice> get() = INSTANCE.boneManIdle
        val boneManPunish: Animation<TextureSlice> get() = INSTANCE.boneManPunish

        val heroIdle: Animation<TextureSlice> get() = INSTANCE.heroIdle
        val heroWalk: Animation<TextureSlice> get() = INSTANCE.heroWalk
        val heroAttack: Animation<TextureSlice> get() = INSTANCE.heroAttack
        val heroDash: Animation<TextureSlice> get() = INSTANCE.heroDash
        val heroSwing: Animation<TextureSlice> get() = INSTANCE.heroSwing
        val heroDie: Animation<TextureSlice> get() = INSTANCE.heroDie
        val heroDead: Animation<TextureSlice> get() = INSTANCE.heroDead
        val heroAir: Animation<TextureSlice> get() = INSTANCE.heroAir

        val swipeAttack1: Animation<TextureSlice> get() = INSTANCE.swipeAttack1
        val swipeAttack2: Animation<TextureSlice> get() = INSTANCE.swipeAttack2
        val swipeAttack3: Animation<TextureSlice> get() = INSTANCE.swipeAttack3
        val boneSpearAttack: Animation<TextureSlice> get() = INSTANCE.boneSpearAttack

        val meatBallStandUp: Animation<TextureSlice> get() = INSTANCE.meatBallStandUp
        val meatBallRun: Animation<TextureSlice> get() = INSTANCE.meatBallRun
        val meatBallSit: Animation<TextureSlice> get() = INSTANCE.meatBallSit
        val meatBallHandOfDeath: Animation<TextureSlice> get() = INSTANCE.meatBallHandOfDeath

        val chickenSpearRun: Animation<TextureSlice> get() = INSTANCE.chickenSpearRun
        val chickenSpearHandOfDeath: Animation<TextureSlice> get() = INSTANCE.chickenSpearHandOfDeath

        val hopperManHop: Animation<TextureSlice> get() = INSTANCE.hopperManHop
        val hopperManAir: Animation<TextureSlice> get() = INSTANCE.hopperManAir

        val beetleRun: Animation<TextureSlice> get() = INSTANCE.beetleRun

        val batFlap: Animation<TextureSlice> get() = INSTANCE.batFlap

        val sfxFootstep get() = INSTANCE.sfxFootstep
        val sfxSwings get() = INSTANCE.sfxSwings
        val sfxHits get() = INSTANCE.sfxHits
        val sfxLands get() = INSTANCE.sfxLands
        val sfxCollect get() = INSTANCE.sfxCollect
        val sfxShoot get() = INSTANCE.sfxShoot
        val sfxSelect get() = INSTANCE.sfxSelect
        val sfxSlam get() = INSTANCE.sfxSlam
        val sfxSkillUnlock get() = INSTANCE.sfxSkillUnlock
        val sfxDeathHero get() = INSTANCE.sfxDeathHero
        val sfxDeathMob get() = INSTANCE.sfxDeathMob

        val music get() = INSTANCE.music

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