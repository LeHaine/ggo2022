package com.lehaine.game.node.ui

import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.node
import com.lehaine.littlekt.graph.node.ui.*
import kotlin.time.Duration

fun Node.upgradesDialog(shouldDisplay: () -> Boolean) = node(UpgradesDialog(shouldDisplay))

/**
 * @author Colton Daily
 * @date 11/11/2022
 */
class UpgradesDialog(private val shouldDisplay: () -> Boolean) : Control() {
    private var lastVisibility = shouldDisplay()

    init {
        anchor(AnchorLayout.CENTER)

        centerContainer {
            anchor(AnchorLayout.CENTER)
            panelContainer {
                paddedContainer {
                    padding(10)
                    paddingTop = 25
                    vBoxContainer {
                        separation = 10
                        button {
                            text = "Option 1"
                        }

                        button {
                            text = "Option 2"
                        }

                        button {
                            text = "Option 3"
                        }

                        button {
                            text = "Final Option"
                        }
                    }
                }
            }
        }
    }


    override fun update(dt: Duration) {
        super.update(dt)

        visible = shouldDisplay()
        if (lastVisibility != visible) {
            // TODO generate options
        }
        lastVisibility = visible
    }
}