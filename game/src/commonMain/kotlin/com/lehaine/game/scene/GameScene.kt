package com.lehaine.game.scene

import com.lehaine.game.*
import com.lehaine.game.node.entity.BoneMan
import com.lehaine.game.node.entity.Hero
import com.lehaine.game.node.entity.SoulItem
import com.lehaine.game.node.entity.hero
import com.lehaine.game.node.entity.mob.Mob
import com.lehaine.game.node.level.TestSpawner
import com.lehaine.game.node.ui.*
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.async.KtScope
import com.lehaine.littlekt.file.ldtk.LDtkMapLoader
import com.lehaine.littlekt.file.vfs.readLDtkMapLoader
import com.lehaine.littlekt.file.vfs.readPixmap
import com.lehaine.littlekt.graph.node.*
import com.lehaine.littlekt.graph.node.resource.AlignMode
import com.lehaine.littlekt.graph.node.resource.HAlign
import com.lehaine.littlekt.graph.node.resource.NinePatchDrawable
import com.lehaine.littlekt.graph.node.node2d.Node2D
import com.lehaine.littlekt.graph.node.node2d.node2d
import com.lehaine.littlekt.graph.node.ui.*
import com.lehaine.littlekt.graphics.Color
import com.lehaine.littlekt.graphics.Cursor
import com.lehaine.littlekt.graphics.g2d.NinePatch
import com.lehaine.littlekt.graphics.g2d.getAnimation
import com.lehaine.littlekt.graphics.g2d.tilemap.ldtk.LDtkLevel
import com.lehaine.littlekt.input.GameAxis
import com.lehaine.littlekt.input.GameButton
import com.lehaine.littlekt.input.Key
import com.lehaine.littlekt.input.Pointer
import com.lehaine.littlekt.math.floorToInt
import com.lehaine.littlekt.math.interpolate
import com.lehaine.littlekt.util.seconds
import com.lehaine.littlekt.util.toString
import com.lehaine.littlekt.util.viewport.ExtendViewport
import com.lehaine.rune.engine.ActionCreator
import com.lehaine.rune.engine.RuneScene
import com.lehaine.rune.engine.node.EntityCamera2D
import com.lehaine.rune.engine.node.entityCamera2D
import com.lehaine.rune.engine.node.pixelPerfectSlice
import com.lehaine.rune.engine.node.pixelSmoothFrameBuffer
import com.lehaine.rune.engine.node.renderable.animatedSprite
import com.lehaine.rune.engine.node.renderable.entity.cd
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


class GameScene(context: Context) :
    RuneScene<GameInput>(
        context,
        ExtendViewport(Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT),
        uiInputSignals = createUiGameInputSignals()
    ) {

    lateinit var upgradesDialog: UpgradesDialog
    lateinit var pauseDialog: PauseDialog
    lateinit var settingsDialog: SettingsDialog
    lateinit var gameCanvas: CanvasLayer
    private var monstersIncomingLabel: Label? = null

    private var setupController = false

    val state = GameState().apply {
        onHealHero = {
            hero.setHealthToFull()
        }
    }

    lateinit var background: Node
    lateinit var fxBackground: Node
    lateinit var main: Node
    lateinit var foreground: Node
    lateinit var fxForeground: Node
    lateinit var top: Node

    lateinit var actionBar: ActionBar
    lateinit var ui: Control

    lateinit var entities: Node2D
    var mapLoader: LDtkMapLoader? = null
    lateinit var ldtkLevel: LDtkLevel
    lateinit var hero: Hero
    lateinit var level: Level
    var boneMan: BoneMan? = null
    private var levelIdx = 0

    private var actionCreator: ActionCreator? = null

    val fx = Fx(this)

    init {
        setupController()
        clearColor = Color.fromHex("#422e37")
    }

    private fun setupController() {
        val isQwerty = Config.keyboardType == Config.KeyboardType.QWERTY
        controller.addBinding(
            GameInput.MOVE_LEFT,
            listOf(
                if (isQwerty) Key.A else Key.Q,
                Key.ARROW_LEFT
            ),
            axes = listOf(GameAxis.LX)
        )
        controller.addBinding(GameInput.MOVE_RIGHT, listOf(Key.D, Key.ARROW_RIGHT), axes = listOf(GameAxis.LX))
        controller.addBinding(
            GameInput.MOVE_UP, listOf(
                if (isQwerty) Key.W else Key.Z,
                Key.ARROW_UP
            ), axes = listOf(GameAxis.LY)
        )
        controller.addBinding(GameInput.MOVE_DOWN, listOf(Key.S, Key.ARROW_DOWN), axes = listOf(GameAxis.LY))

        controller.addBinding(
            GameInput.SWING,
            buttons = listOf(GameButton.RIGHT_TRIGGER),
            pointers = listOf(Pointer.MOUSE_LEFT)
        )
        controller.addBinding(
            GameInput.SHOOT,
            buttons = listOf(GameButton.XBOX_X),
            pointers = listOf(Pointer.MOUSE_RIGHT, Pointer.MOUSE_MIDDLE)
        )
        controller.addBinding(
            GameInput.DASH,
            buttons = listOf(GameButton.XBOX_A),
            keys = listOf(Key.SHIFT_LEFT, Key.SPACE)
        )
        controller.addBinding(
            GameInput.HAND_OF_DEATH,
            buttons = listOf(GameButton.XBOX_Y),
            keys = if (Config.keyboardType == Config.KeyboardType.QWERTY) listOf(Key.Q) else listOf(Key.A)
        )
        controller.addBinding(
            GameInput.BONE_SPEAR,
            buttons = listOf(GameButton.XBOX_B),
            keys = listOf(Key.E)
        )

        controller.addAxis(GameInput.HORIZONTAL, GameInput.MOVE_RIGHT, GameInput.MOVE_LEFT)
        controller.addAxis(GameInput.VERTICAL, GameInput.MOVE_DOWN, GameInput.MOVE_UP)

        controller.addVector(
            GameInput.MOVEMENT,
            GameInput.MOVE_RIGHT,
            GameInput.MOVE_DOWN,
            GameInput.MOVE_LEFT,
            GameInput.MOVE_UP
        )

        controller.addBinding(GameInput.PAUSE, keys = listOf(Key.ESCAPE), buttons = listOf(GameButton.START))

    }


    override suspend fun Node.initialize() {
        mapLoader?.dispose()
        val mapLoader =
            resourcesVfs["world.ldtk"].readLDtkMapLoader().also { this@GameScene.mapLoader = it }
        val world = mapLoader.loadMap(false, 0)
        ldtkLevel = world.levels[0]
        val cursorImage = resourcesVfs["cursor.png"].readPixmap()
        val cursor =
            Cursor(cursorImage, (cursorImage.width * 0.5).roundToInt(), (cursorImage.height * 0.5f).roundToInt())
        context.graphics.setCursor(cursor)
        Assets.music.play(0.05f * Config.musicMultiplier, true)
        createNodes()
    }

    private fun Node.createNodes() {
        gameCanvas = canvasLayer {
            val entityCamera: EntityCamera2D

            val fbo = pixelSmoothFrameBuffer {
                targetHeight = 235
                entityCamera = entityCamera2D {
                    trackingSpeed = 1.25f
                    clampToBounds = false
                    viewBounds.width = ldtkLevel.pxWidth.toFloat()
                    viewBounds.height = ldtkLevel.pxHeight.toFloat()
                    camera = canvasCamera
                }

                background = node {
                    name = "Background"

                    level = Level(ldtkLevel).addTo(this)
                }

                fxBackground = node {
                    name = "FX Background"
                }

                main = node {
                    name = "Main"


                    val projectiles = Node2D().apply {
                        name = "Projectiles"
                    }
                    entities = node2d {
                        name = "Entities"
                        ySort = true

                        hero = hero(ldtkLevel.entities("Hero")[0], level, entityCamera, projectiles) {
                            onDeath += {
                                ui.apply {
                                    fadeMask(
                                        250.milliseconds,
                                        1.seconds,
                                        Color.fromHex("#994551"),
                                        FadeMask.Fade.IN
                                    )
                                }
                                cd("fade", 2500.milliseconds) {
                                    loadLevel(1) { onEnterBoneMansOffice() }
                                }
                            }
                        }
                        entityCamera.follow(hero, true)

                        if (ldtkLevel.entitiesByIdentifier.contains("BoneMan")) {
                            ldtkLevel.entities("BoneMan").firstOrNull()?.let {
                                boneMan = BoneMan(it).addTo(this)
                            }
                        }
                    }

                    SoulItem.initPool(level, entities)

                    projectiles.addTo(this)

                    if (ldtkLevel.entitiesByIdentifier.contains("MonsterSpawner")) {
                        TestSpawner(hero, level).apply {
                            onMajorEvent += {
                                monstersIncomingLabel?.visible = true
                            }
                        }.addTo(this)
                    }
                }

                foreground = node {
                    name = "Foreground"
                }

                fxForeground = node {
                    name = "FX Foreground"
                }

                top = node {
                    name = "Top"
                }
            }

            pixelPerfectSlice {
                this.fbo = fbo
                onUpdate += {
                    scaledDistX = entityCamera.scaledDistX
                    scaledDistY = entityCamera.scaledDistY
                }
            }
        }

        ui = control {
            name = "UI"
            anchorRight = 1f
            anchorBottom = 1f

            paddedContainer {
                padding(10)
                paddingTop = 20
                anchor(Control.AnchorLayout.TOP_RIGHT)
                label {
                    horizontalAlign = HAlign.RIGHT
                    onUpdate += {
                        text = if (showDebugInfo) {
                            stats.fps.toString(1)
                        } else {
                            ""
                        }
                    }
                }
            }
            panel {
                name = "Border"
                val border9p = NinePatch(Assets.atlas.getByPrefix("uiArenaBorder").slice, 16, 16, 15, 15)
                panel = NinePatchDrawable(border9p)
                anchorRight = 1f
                anchorBottom = 1f
            }

            paddedContainer {
                paddingTop = 15
                paddingRight = 25
                anchor(Control.AnchorLayout.TOP_RIGHT)
                column {
                    label {
                        text = "Quota: ${state.soulsCaptured}/${state.nextUnlockCost}"
                        horizontalAlign = HAlign.RIGHT
                        onUpdate += {
                            if (state.soulsCaptured < state.nextUnlockCost) {
                                text = "Quota: ${state.soulsCaptured}/${state.nextUnlockCost}"
                                color = Color.RED
                            } else {
                                text = "Quota reached.\nDie to meet the bone man."
                                color = Color.GREEN
                            }
                        }
                    }
                }
            }
            actionBar = actionBar()

            row {
                marginLeft = 15f
                marginTop = 15f
                separation = 50

                column {
                    separation = 30
                    val deadHeart = Assets.atlas.getAnimation("heartDead")

                    var lastHeroMultiplier = 0f
                    var lastBaseHealth = 0

                    onUpdate += {
                        if (lastHeroMultiplier != state.heroHealthMultiplier || lastBaseHealth != state.heroBaseHealth) {
                            lastHeroMultiplier = state.heroHealthMultiplier
                            lastBaseHealth = state.heroBaseHealth
                            destroyAllChildren()
                            val newHealth =
                                (state.heroBaseHealth * state.heroHealthMultiplier).floorToInt().coerceAtLeast(1)
                            if (hero.health > newHealth) {
                                hero.health = newHealth
                            }
                            repeat(newHealth) {
                                control {
                                    animatedSprite {
                                        val idx = it
                                        onReady += {
                                            registerState(Assets.heartBeating, priority = 5) { hero.health >= idx + 1 }
                                            registerState(deadHeart, 0)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (levelIdx == 0) {
                    label {
                        name = "Timer"
                        verticalSizeFlags = Control.SizeFlag.FILL
                        var timer = Duration.ZERO
                        onUpdate += {
                            if (gameCanvas.updateInterval != 0) {
                                val minutes = (timer.seconds / 60).floorToInt()
                                val seconds = (timer.seconds % 60).floorToInt()

                                val minutesStr = if (minutes < 10) {
                                    "0$minutes"
                                } else {
                                    "$minutes"
                                }
                                val secondsStr = if (seconds < 10) {
                                    "0$seconds"
                                } else {
                                    "$seconds"
                                }
                                text = "$minutesStr:$secondsStr"
                                timer += it
                            }
                        }
                    }
                }
            }

            paddedContainer {
                padding(15)
                anchor(Control.AnchorLayout.CENTER_TOP)
                progressBar {
                    minWidth = 200f
                    onUpdate += {
                        ratio = state.exp.ratioToNextLevel
                    }
                }
            }

            monstersIncomingLabel = label {
                horizontalAlign = HAlign.CENTER
                anchorRight = 1f
                marginTop = 100f

                text = "Monsters incoming!"
                visible = levelIdx == 0

                var forward = true
                var timer = Duration.ZERO
                var duration = Duration.ZERO

                onReady += {
                    visible = false
                }
                onUpdate += {
                    if (visible) {
                        val ratio = timer.seconds / 0.5f
                        fontScaleX = ratio.interpolate(1f, 3f)
                        fontScaleY = ratio.interpolate(1f, 3f)

                        if (forward) {
                            timer += it
                        } else {
                            timer -= it
                        }
                        if (ratio >= 1f) {
                            forward = false
                            Assets.sfxWarning.play(0.1f * Config.sfxMultiplier)
                        } else if (ratio <= 0f) {
                            forward = true
                        }


                        duration += it
                        if (duration >= 5.seconds) {
                            visible = false
                            timer = Duration.ZERO
                            duration = Duration.ZERO
                        }
                    }
                }
            }

            upgradesDialog = upgradesDialog(state) {
                enabled = false
                onUpgradeSelect += {
                    enabled = false
                    gameCanvas.updateInterval = 1
                }
            }

            settingsDialog = settingsDialog {
                enabled = false
                onBack += {
                    pauseDialog.enabled = true
                    enabled = false
                }

                onKeyboardChange += {
                    setupController = true
                }
            }

            pauseDialog = pauseDialog {
                enabled = false
                onResume += {
                    enabled = false
                    gameCanvas.updateInterval = 1
                }

                onSettings += {
                    enabled = false
                    settingsDialog.enabled = true
                }
            }

            fadeMask(
                delay = 250.milliseconds,
                fadeTime = 1.seconds,
                fadeColor = if (levelIdx == 1) Color.fromHex("#994551") else Color.fromHex("#332e30")
            ) {
                onFinish += { destroy() }
            }
        }

        state.exp.onLevelUp +=
            { level, gained ->
                gameCanvas.updateInterval = 0
                upgradesDialog.enabled = true
                upgradesDialog.refresh()
            }

        fx.createParticleBatchNodes()
    }

    override fun update(dt: Duration) {
        if (gameCanvas.updateInterval == 1) {
            fx.update(dt)
            actionCreator?.execute(dt)
        }
        super.update(dt)

        if (controller.pressed(GameInput.PAUSE) && gameCanvas.updateInterval == 1) {
            pauseDialog.enabled = true
            gameCanvas.updateInterval = 0
        } else if (controller.pressed(GameInput.PAUSE) && gameCanvas.updateInterval != 1 && pauseDialog.enabled) {
            pauseDialog.enabled = false
            gameCanvas.updateInterval = 1
        }

        if (input.isKeyJustPressed(Key.T)) {
            println(root.treeString())
        }

        if (input.isKeyJustPressed(Key.P)) {
            println(stats)
        }

        if (setupController) {
            setupController()
            setupController = false
        }
    }

    private fun onEnterBoneMansOffice() {
        Mob.ALL.clear()
        SoulItem.ALL.clear()
        SoulItem.MARKED.clear()
        Assets.music.pause()
        if (state.soulsCaptured >= state.nextUnlockCost) {
            performQuotaMetAnimation()
        } else {
            state.quotasFailed++
            performQuotaFailedAnimation()
        }
    }

    private fun onEnterArena() {
        actionCreator = null
        Assets.music.resume()
    }

    private fun performQuotaMetAnimation() {
        val labelColumn = VBoxContainer().apply {
            separation = 50
            align = AlignMode.CENTER
            anchor(Control.AnchorLayout.CENTER)
        }
        val container = CenterContainer().apply {
            anchorBottom = 1f
            anchorRight = 1f
            addChild(labelColumn)
        }.addTo(ui)
        val quotaLabel = Label().apply {
            text = "QUOTA"
            font = Assets.pixelFontOutline
            visible = false
            fontScaleX = 5f
            fontScaleY = 5f
            horizontalAlign = HAlign.CENTER
        }.addTo(labelColumn)

        val metaLabel = Label().apply {
            text = "MET"
            font = Assets.pixelFontOutline
            visible = false
            fontScaleX = 5f
            fontScaleY = 5f
            horizontalAlign = HAlign.CENTER
        }.addTo(labelColumn)

        actionCreator = ActionCreator {
            wait(1000.milliseconds) {
                quotaLabel.visible = true
                hero.camera.shake(100.milliseconds, 1f * Config.cameraShakeMultiplier)
                Assets.sfxSlam.play(0.5f * Config.sfxMultiplier)
            }
            wait(1000.milliseconds) {
                metaLabel.visible = true
                hero.camera.shake(100.milliseconds, 1f * Config.cameraShakeMultiplier)
                Assets.sfxSlam.play(0.5f * Config.sfxMultiplier)
            }

            wait(3.seconds) { container.destroy() }

            if (state.unlockIdx < 4) {
                wait(1000.milliseconds) {
                    boneMan?.sprite?.playOnce(Assets.boneManPunish)
                }
                wait(Assets.boneManPunish.duration) {
                    state.unlockNextSkill()
                    hero.levelUp()
                    hero.camera.shake(100.milliseconds, 1f * Config.cameraShakeMultiplier)
                    Assets.sfxSkillUnlock.play(0.3f * Config.sfxMultiplier)
                }
                wait(1500.milliseconds) {
                    loadLevel(0) { onEnterArena() }
                }
            } else {
                action {
                    changeTo(GameOverScene(true, context))
                }
            }
        }
    }

    private fun performQuotaFailedAnimation() {
        val labelColumn = VBoxContainer().apply {
            separation = 50
            align = AlignMode.CENTER
            anchor(Control.AnchorLayout.CENTER)
        }
        val container = CenterContainer().apply {
            anchorBottom = 1f
            anchorRight = 1f
            addChild(labelColumn)
        }.addTo(ui)
        val quotaLabel = Label().apply {
            text = "QUOTA"
            font = Assets.pixelFontOutline
            visible = false
            fontScaleX = 5f
            fontScaleY = 5f
            fontColor = Color.fromHex("#f2e6e6")
            horizontalAlign = HAlign.CENTER
        }.addTo(labelColumn)

        val metaLabel = Label().apply {
            text = "FAILED"
            font = Assets.pixelFontOutline
            visible = false
            fontScaleX = 5f
            fontScaleY = 5f
            fontColor = Color.fromHex("#994551")
            horizontalAlign = HAlign.CENTER
        }.addTo(labelColumn)

        val quotasFailed = Label().apply {
            name = "quotas"
            text = "Quotas failed ${state.quotasFailed}/4"
            font = Assets.pixelFontOutline
            fontScaleX = 4f
            fontScaleY = 4f
            fontColor = Color.fromHex("#994551")
            horizontalAlign = HAlign.CENTER
        }

        actionCreator = ActionCreator {
            wait(1000.milliseconds) {
                quotaLabel.visible = true
                hero.camera.shake(100.milliseconds, 1f * Config.cameraShakeMultiplier)
                Assets.sfxSlam.play(0.5f * Config.sfxMultiplier)
            }
            wait(1000.milliseconds) {
                metaLabel.visible = true
                hero.camera.shake(100.milliseconds, 1f * Config.cameraShakeMultiplier)
                Assets.sfxSlam.play(0.5f * Config.sfxMultiplier)
            }
            wait(1500.milliseconds) {
                quotaLabel.destroy()
                metaLabel.destroy()
                quotasFailed.addTo(labelColumn)
            }
            wait(3.seconds) {
                container.destroy()
                if (state.quotasFailed == 4) {
                    changeTo(GameOverScene(false, context))
                } else {
                    loadLevel(0) { onEnterArena() }
                }
            }
        }
    }

    private fun loadLevel(idx: Int, onLaunch: () -> Unit = {}) {
        levelIdx = idx
        mapLoader?.let {
            KtScope.launch {
                val world = it.loadMap(false, idx)
                ldtkLevel = world.levels[0]

                root.destroyAllChildren()
                root.createNodes()
                resize(graphics.width, graphics.height)
                onLaunch()
            }
        }
    }

    fun flashRed() {
        ui.apply {
            fadeMask(Duration.ZERO, fadeTime = 100.milliseconds, fadeColor = Color.fromHex("#994551")) {
                onFinish += {
                    destroy()
                }
            }
        }
    }
}