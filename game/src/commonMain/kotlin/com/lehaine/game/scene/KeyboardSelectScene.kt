package com.lehaine.game.scene

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.node.ui.fadeMask
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.component.AlignMode
import com.lehaine.littlekt.graph.node.component.HAlign
import com.lehaine.littlekt.graph.node.ui.*
import com.lehaine.littlekt.util.viewport.ExtendViewport
import com.lehaine.rune.engine.RuneSceneDefault
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * @author Colton Daily
 * @date 11/11/2022
 */
class KeyboardSelectScene(
    context: Context
) : RuneSceneDefault(context, ExtendViewport(Config.VIRTUAL_WIDTH, Config.VIRTUAL_HEIGHT)) {

    private var switchingScenes = false
    override suspend fun Node.initialize() {
        centerContainer {
            anchorRight = 1f
            anchorBottom = 1f

            panelContainer {
                paddedContainer {
                    padding(10)
                    column {
                        separation = 10
                        align = AlignMode.CENTER
                        label {
                            text = "Select your keyboard type\nor update in settings later:"
                            font = Assets.pixelFont
                            horizontalAlign = HAlign.CENTER
                        }

                        row {
                            separation = 10
                            align = AlignMode.CENTER
                            button {
                                text = "QWERTY"

                                onReady += {
                                    requestFocus(this)
                                }
                                onPressed += {
                                    selectKeyboard(Config.KeyboardType.QWERTY)
                                }
                            }

                            button {
                                text = "AZERTY"

                                onPressed += {
                                    selectKeyboard(Config.KeyboardType.AZERTY)
                                }
                            }
                        }
                    }
                }
            }
        }
        fadeMask(delay = 250.milliseconds, fadeTime = 1.seconds)
    }

    private fun selectKeyboard(keyboardType: Config.KeyboardType) {
        Config.keyboardType = keyboardType
        if (!switchingScenes) {
            switchingScenes = true
            changeTo(MenuScene(context))
        }
    }
}