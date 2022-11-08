package com.lehaine.game.scene

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.component.HAlign
import com.lehaine.littlekt.graph.node.ui.*
import com.lehaine.littlekt.util.viewport.ExtendViewport
import com.lehaine.rune.engine.RuneSceneDefault


class MenuScene(
    context: Context
) : RuneSceneDefault(context, ExtendViewport(Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT)) {

    override suspend fun Node.initialize() {
        centerContainer {
            anchorRight = 1f
            anchorBottom = 1f

            panelContainer {
                paddedContainer {
                    paddingTop = 25
                    paddingLeft = 10
                    paddingBottom = 10
                    paddingRight = 10
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
    }
}