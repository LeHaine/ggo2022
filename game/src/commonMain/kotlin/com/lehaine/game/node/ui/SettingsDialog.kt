package com.lehaine.game.node.ui

import com.lehaine.game.Config
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.component.AlignMode
import com.lehaine.littlekt.graph.node.component.HAlign
import com.lehaine.littlekt.graph.node.node
import com.lehaine.littlekt.graph.node.ui.*
import com.lehaine.littlekt.util.signal

fun Node.settingsDialog(callback: SettingsDialog.() -> Unit = {}) = node(SettingsDialog(), callback)

/**
 * @author Colton Daily
 * @date 11/12/2022
 */
class SettingsDialog : CenterContainer() {

    val onKeyboardChange = signal()
    val onBack = signal()

    init {
        anchorRight = 1f
        anchorBottom = 1f

        panelContainer {
            paddedContainer {
                padding(10)
                column {
                    separation = 10
                    label {
                        text = "Settings"
                        horizontalAlign = HAlign.CENTER
                    }

                    label {
                        text = "Music:"
                    }

                    row {
                        name = "Music Container"
                        separation = 5
                        align = AlignMode.CENTER
                        val progressBar = ProgressBar()
                        soundButton {
                            text = "-"
                            onUpdate += {
                                if (pressed) {
                                    progressBar.value -= progressBar.step
                                }
                            }
                        }
                        node(progressBar) {
                            ratio = Config.musicMultiplier
                            minWidth = 100f
                            onValueChanged += {
                                Config.musicMultiplier = ratio
                            }
                        }
                        soundButton {
                            text = "+"
                            onUpdate += {
                                if (pressed) {
                                    progressBar.value += progressBar.step
                                }
                            }
                        }
                    }

                    label {
                        text = "Sfx:"
                    }

                    row {
                        name = "Sfx Container"
                        separation = 5
                        align = AlignMode.CENTER
                        val progressBar = ProgressBar()
                        soundButton {
                            text = "-"
                            onUpdate += {
                                if (pressed) {
                                    progressBar.value -= progressBar.step
                                }
                            }
                        }
                        node(progressBar) {
                            ratio = Config.sfxMultiplier
                            minWidth = 100f
                            onValueChanged += {
                                Config.sfxMultiplier = ratio
                            }
                        }
                        soundButton {
                            text = "+"
                            onUpdate += {
                                if (pressed) {
                                    progressBar.value += progressBar.step
                                }
                            }
                        }
                    }


                    label {
                        text = "Camera Shake:"
                    }

                    row {
                        name = "Camera Shake Container"
                        separation = 5
                        align = AlignMode.CENTER
                        val progressBar = ProgressBar()
                        soundButton {
                            text = "-"
                            onUpdate += {
                                if (pressed) {
                                    progressBar.value -= progressBar.step
                                }
                            }
                        }
                        node(progressBar) {
                            ratio = Config.cameraShakeMultiplier
                            minWidth = 100f
                            onValueChanged += {
                                Config.cameraShakeMultiplier = ratio
                            }
                        }
                        soundButton {
                            text = "+"
                            onUpdate += {
                                if (pressed) {
                                    progressBar.value += progressBar.step
                                }
                            }
                        }
                    }
                    label {
                        text = "Keyboard Type:"
                    }
                    row {
                        separation = 10
                        align = AlignMode.CENTER

                        val keyboardButtonGroup = ButtonGroup()
                        soundButton {
                            text = "QWERTY"
                            toggleMode = true
                            setButtonGroup(keyboardButtonGroup)
                            pressed = Config.keyboardType == Config.KeyboardType.QWERTY
                            onPressed += {
                                Config.keyboardType = Config.KeyboardType.QWERTY
                                onKeyboardChange.emit()
                            }
                        }

                        soundButton {
                            text = "AZERTY"
                            toggleMode = true
                            pressed = Config.keyboardType == Config.KeyboardType.AZERTY
                            setButtonGroup(keyboardButtonGroup)
                            pressed = Config.keyboardType == Config.KeyboardType.AZERTY
                            onPressed += {
                                Config.keyboardType = Config.KeyboardType.AZERTY
                                onKeyboardChange.emit()
                            }
                        }
                    }

                    soundButton {
                        text = "Back"
                        onPressed += {
                            onBack.emit()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onBack.clear()
        onKeyboardChange.clear()
    }
}