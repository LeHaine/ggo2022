package com.lehaine.game.node.ui

import com.lehaine.littlekt.Context
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.resource.HAlign
import com.lehaine.littlekt.graph.node.node
import com.lehaine.littlekt.graph.node.ui.*
import com.lehaine.littlekt.util.signal


fun Node.pauseDialog(callback: PauseDialog.() -> Unit) = node(PauseDialog(), callback)

/**
 * @author Colton Daily
 * @date 11/12/2022
 */
class PauseDialog : CenterContainer() {

    val onResume = signal()
    val onSettings = signal()

    init {
        anchorRight = 1f
        anchorBottom = 1f

        panelContainer {
            paddedContainer {
                padding(10)
                column {
                    separation = 10
                    label {
                        text = "Paused"
                        horizontalAlign = HAlign.CENTER
                    }


                    soundButton {
                        text = "Resume"
                        onReady += {
                            scene?.requestFocus(this)
                        }
                        onPressed += {
                            onResume.emit()
                        }
                    }

                    soundButton {
                        text = "Settings"
                        onPressed += {
                            onSettings.emit()
                        }
                    }

                    soundButton {
                        onReady += {
                            if (context.platform != Context.Platform.DESKTOP) {
                                enabled = false
                            }
                        }
                        text = "Quit"
                        onPressed += {
                            context.close()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onResume.clear()
        onSettings.clear()
    }
}