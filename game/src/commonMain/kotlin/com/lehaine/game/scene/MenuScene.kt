package com.lehaine.game.scene

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.node.ui.fadeMask
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.component.HAlign
import com.lehaine.littlekt.graph.node.ui.*
import com.lehaine.littlekt.util.viewport.ExtendViewport
import com.lehaine.rune.engine.RuneSceneDefault
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


class MenuScene(
    context: Context
) : RuneSceneDefault(context, ExtendViewport(Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT)) {

    override suspend fun Node.initialize() {
        centerContainer {
            anchorRight = 1f
            anchorBottom = 1f

            panelContainer {
                paddedContainer {
                    padding(10)
                    paddingTop = 25
                    vBoxContainer {
                        separation = 10

                        label {
                            text = "Main Menu"
                            font = Assets.pixelFont
                            horizontalAlign = HAlign.CENTER
                            fontScaleX = 2f
                            fontScaleY = 2f
                        }

                        button {
                            var startingGame = false
                            text = "Start Game"

                            onPressed += {
                                if (!startingGame) {
                                    startingGame = true
                                    changeTo(GameScene(context))
                                }
                            }
                        }

                        button {
                            text = "Settings"
                        }
                    }
                }
            }
        }

        fadeMask(delay = 250.milliseconds, fadeTime = 1.seconds)
    }
}