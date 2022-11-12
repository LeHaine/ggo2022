package com.lehaine.game.scene

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
class SettingsScene(
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
                    vBoxContainer {
                        separation = 10
                        label {
                            text = "Settings"
                            horizontalAlign = HAlign.CENTER
                        }

                        label {
                            text = "Keyboard Type:"
                        }
                        hBoxContainer {
                            separation = 10
                            align = AlignMode.CENTER

                            val keyboardButtonGroup = ButtonGroup()
                            button {
                                text = "QWERTY"
                                toggleMode = true
                                buttonGroup = keyboardButtonGroup
                                buttonGroup.buttons += this
                                pressed = Config.keyboardType == Config.KeyboardType.QWERTY
                                onPressed += {
                                    Config.keyboardType = Config.KeyboardType.QWERTY
                                }
                            }

                            button {
                                text = "AZERTY"
                                toggleMode = true
                                pressed = Config.keyboardType == Config.KeyboardType.AZERTY
                                buttonGroup = keyboardButtonGroup
                                buttonGroup.buttons += this
                                pressed = Config.keyboardType == Config.KeyboardType.AZERTY
                                onPressed += {
                                    Config.keyboardType = Config.KeyboardType.AZERTY
                                }
                            }
                        }

                        button {
                            text = "Back"
                            onPressed += {
                                if (!switchingScenes) {
                                    switchingScenes = true
                                    changeTo(MenuScene(context))
                                }
                            }
                        }
                    }
                }
            }
        }
        fadeMask(delay = 250.milliseconds, fadeTime = 1.seconds)
    }
}