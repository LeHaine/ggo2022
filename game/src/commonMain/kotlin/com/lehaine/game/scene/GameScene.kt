package com.lehaine.game.scene

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.Fx
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.canvasLayer
import com.lehaine.littlekt.graph.node.component.HAlign
import com.lehaine.littlekt.graph.node.component.VAlign
import com.lehaine.littlekt.graph.node.node
import com.lehaine.littlekt.graph.node.ui.Control
import com.lehaine.littlekt.graph.node.ui.control
import com.lehaine.littlekt.graph.node.ui.label
import com.lehaine.littlekt.input.Key
import com.lehaine.littlekt.util.viewport.ExtendViewport
import com.lehaine.rune.engine.RuneScene
import com.lehaine.rune.engine.node.EntityCamera2D
import com.lehaine.rune.engine.node.entityCamera2D
import com.lehaine.rune.engine.node.pixelPerfectSlice
import com.lehaine.rune.engine.node.pixelSmoothFrameBuffer
import kotlin.time.Duration


class GameScene(context: Context) :
    RuneScene(context, ExtendViewport(Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT)) {
    lateinit var background: Node
    lateinit var fxBackground: Node
    lateinit var main: Node
    lateinit var foreground: Node
    lateinit var fxForeground: Node
    lateinit var top: Node
    lateinit var ui: Control

    val fx = Fx(this)

    override suspend fun Node.initialize() {
        createNodes()
    }

    private fun Node.createNodes() {
        canvasLayer {
            val entityCamera: EntityCamera2D

            val fbo = pixelSmoothFrameBuffer {
                entityCamera = entityCamera2D {
                    // TODO set viewbounds
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
                background = node {
                    name = "Background"
                }

                fxBackground = node {
                    name = "FX Background"
                }

                main = node {
                    name = "Main"

                    // TODO create level

                    // TODO create hero
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

                // TODO follow hero
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

            label {
                text = "TODO: Implement game logic"
                font = Assets.pixelFont
                anchorRight = 1f
                anchorBottom = 1f
                verticalAlign = VAlign.CENTER
                horizontalAlign = HAlign.CENTER
            }
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

        if(input.isKeyJustPressed(Key.T)) {
            println(root.treeString())
        }

        if(input.isKeyJustPressed(Key.P)) {
            println(stats)
        }
    }
}