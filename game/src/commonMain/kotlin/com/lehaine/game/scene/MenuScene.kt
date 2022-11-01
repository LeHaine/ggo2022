package com.lehaine.game.scene

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.component.HAlign
import com.lehaine.littlekt.graph.node.ui.button
import com.lehaine.littlekt.graph.node.ui.centerContainer
import com.lehaine.littlekt.graph.node.ui.label
import com.lehaine.littlekt.graph.node.ui.vBoxContainer
import com.lehaine.littlekt.util.viewport.ExtendViewport
import com.lehaine.rune.engine.RuneScene


class MenuScene(
    context: Context
) : RuneScene(context, ExtendViewport(Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT)) {

    override suspend fun Node.initialize() {
        centerContainer {
            anchorRight = 1f
            anchorBottom = 1f

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