package com.lehaine.game.scene

import com.lehaine.game.Config
import com.lehaine.game.Fx
import com.lehaine.game.GameInput
import com.lehaine.game.createUiGameInputSignals
import com.lehaine.game.entity.Hero
import com.lehaine.game.entity.hero
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.file.ldtk.LDtkMapLoader
import com.lehaine.littlekt.file.vfs.readLDtkMapLoader
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.addTo
import com.lehaine.littlekt.graph.node.canvasLayer
import com.lehaine.littlekt.graph.node.node
import com.lehaine.littlekt.graph.node.node2d.Node2D
import com.lehaine.littlekt.graph.node.node2d.node2d
import com.lehaine.littlekt.graph.node.ui.Control
import com.lehaine.littlekt.graph.node.ui.control
import com.lehaine.littlekt.graphics.Color
import com.lehaine.littlekt.graphics.tilemap.ldtk.LDtkLevel
import com.lehaine.littlekt.input.GameAxis
import com.lehaine.littlekt.input.GameButton
import com.lehaine.littlekt.input.Key
import com.lehaine.littlekt.input.Pointer
import com.lehaine.littlekt.util.viewport.ExtendViewport
import com.lehaine.rune.engine.RuneScene
import com.lehaine.rune.engine.node.EntityCamera2D
import com.lehaine.rune.engine.node.entityCamera2D
import com.lehaine.rune.engine.node.pixelPerfectSlice
import com.lehaine.rune.engine.node.pixelSmoothFrameBuffer
import com.lehaine.rune.engine.node.renderable.LDtkGameLevelRenderable
import com.lehaine.rune.engine.node.renderable.ldtkLevel
import kotlin.time.Duration


class GameScene(context: Context) :
    RuneScene<GameInput>(
        context,
        ExtendViewport(Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT),
        uiInputSignals = createUiGameInputSignals()
    ) {
    lateinit var background: Node
    lateinit var fxBackground: Node
    lateinit var main: Node
    lateinit var foreground: Node
    lateinit var fxForeground: Node
    lateinit var top: Node
    lateinit var ui: Control

    lateinit var entities: Node2D
    var mapLoader: LDtkMapLoader? = null
    lateinit var ldtkLevel: LDtkLevel
    lateinit var hero: Hero

    val fx = Fx(this)

    init {
        controller.addBinding(GameInput.MOVE_LEFT, listOf(Key.A, Key.ARROW_LEFT), axes = listOf(GameAxis.LX))
        controller.addBinding(GameInput.MOVE_RIGHT, listOf(Key.D, Key.ARROW_RIGHT), axes = listOf(GameAxis.LX))
        controller.addBinding(GameInput.MOVE_UP, listOf(Key.W, Key.ARROW_UP), axes = listOf(GameAxis.LY))
        controller.addBinding(GameInput.MOVE_DOWN, listOf(Key.S, Key.ARROW_DOWN), axes = listOf(GameAxis.LY))

        controller.addBinding(
            GameInput.ATTACK,
            buttons = listOf(GameButton.XBOX_X),
            pointers = listOf(Pointer.MOUSE_LEFT)
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

        clearColor = Color.fromHex("#422e37")
    }


    override suspend fun Node.initialize() {
        mapLoader?.dispose()
        val mapLoader = resourcesVfs["world.ldtk"].readLDtkMapLoader().also { this@GameScene.mapLoader = it }
        val world = mapLoader.loadMap(false, 0)
        ldtkLevel = world.levels[0]
        createNodes()
    }

    private fun Node.createNodes() {
        canvasLayer {
            val entityCamera: EntityCamera2D

            val fbo = pixelSmoothFrameBuffer {
                entityCamera = entityCamera2D {
                    viewBounds.width = ldtkLevel.pxWidth.toFloat()
                    viewBounds.height = ldtkLevel.pxHeight.toFloat()
                    camera = canvasCamera

                    onUpdate += {
                        if (input.isKeyJustPressed(Key.Z)) {
                            targetZoom = 0.5f
                        }
                        if (input.isKeyJustPressed(Key.X)) {
                            targetZoom = 1f
                        }
                        if (input.isKeyJustPressed(Key.C)) {
                            targetZoom = 2f
                        }
                    }
                }

                val level: LDtkGameLevelRenderable<String>

                background = node {
                    name = "Background"

                    level = ldtkLevel(ldtkLevel) {
                        gridSize = Config.GRID_CELL_SIZE
                    }
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

                        hero = hero(ldtkLevel.entities("Hero")[0], level, entityCamera, projectiles)
                        entityCamera.follow(hero, true)
                    }

                    projectiles.addTo(this)
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

        }
        fx.createParticleBatchNodes()
    }

    override fun update(dt: Duration) {
        fx.update(dt)
        super.update(dt)

        if (input.isKeyJustPressed(Key.R)) {
            destroyRoot()
            root.createNodes()
            resize(graphics.width, graphics.height)
        }

        if (input.isKeyJustPressed(Key.T)) {
            println(root.treeString())
        }

        if (input.isKeyJustPressed(Key.P)) {
            println(stats)
        }
    }
}