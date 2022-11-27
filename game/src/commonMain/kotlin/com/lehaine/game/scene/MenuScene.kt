package com.lehaine.game.scene

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.node.ui.fadeMask
import com.lehaine.game.node.ui.soundButton
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.file.vfs.readLDtkMapLoader
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.ui.*
import com.lehaine.littlekt.graphics.Color
import com.lehaine.littlekt.util.viewport.ExtendViewport
import com.lehaine.rune.engine.RuneSceneDefault
import com.lehaine.rune.engine.node.renderable.animatedSprite
import com.lehaine.rune.engine.node.renderable.ldtkLevel
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


class MenuScene(
    context: Context
) : RuneSceneDefault(context, ExtendViewport(Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT)) {

    private var switchingScenes = false

    init {
        clearColor = Color.fromHex("#422e37")
    }
    override suspend fun Node.initialize() {
        val mapLoader = resourcesVfs["world.ldtk"].readLDtkMapLoader()
        val world = mapLoader.loadMap(false, 2)
        val ldtkLevel = world.levels[0]
        ldtkLevel<String>(ldtkLevel)
        animatedSprite {
            scaleX = 4f
            scaleY = 4f

            x -= 16f
            y = 112f

            playLooped(Assets.boneManIdle)
        }
        textureRect {
            slice = Assets.atlas.getByPrefix("titleHalf").slice
            anchor(Control.AnchorLayout.CENTER_TOP)
        }
        centerContainer {
            anchor(Control.AnchorLayout.CENTER_BOTTOM)

            panelContainer {
                paddedContainer {
                    padding(10)
                    paddingTop = 5

                    column {
                        separation = 15

                        column {
                            separation = 10
                            soundButton {
                                text = "Start Game"
                                requestFocus(this)
                                onPressed += {
                                    if (!switchingScenes) {
                                        switchingScenes = true
                                        changeTo(GameScene(context))
                                    }
                                }
                            }

                            soundButton {
                                text = "Settings"
                                onPressed += {
                                    if (!switchingScenes) {
                                        switchingScenes = true
                                        changeTo(SettingsScene(context))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        fadeMask(delay = 250.milliseconds, fadeTime = 1.seconds) { onFinish += { destroy() } }
    }
}