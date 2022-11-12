package com.lehaine.game.node.ui

import com.lehaine.game.data.createUpgrades
import com.lehaine.game.scene.GameState
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.node
import com.lehaine.littlekt.graph.node.ui.*
import com.lehaine.littlekt.util.signal

fun Node.upgradesDialog(state: GameState, callback: UpgradesDialog.() -> Unit = {}) =
    node(UpgradesDialog(state), callback)

/**
 * @author Colton Daily
 * @date 11/11/2022
 */
class UpgradesDialog(private val state: GameState) : Control() {

    private val upgrades = createUpgrades(state)
    private val buttonColumn: VBoxContainer

    val onUpgradeSelect = signal()

    init {
        anchor(AnchorLayout.CENTER)

        centerContainer {
            anchor(AnchorLayout.CENTER)
            panelContainer {
                paddedContainer {
                    padding(10)
                    paddingTop = 25
                    buttonColumn = column {
                        separation = 10
                    }
                }
            }
        }
    }

    fun refresh() {
        while (buttonColumn.childCount > 0) {
            buttonColumn.removeChildAt(0)
        }
        buttonColumn.apply {
            repeat(3) {
                val upgrade = upgrades.random()
                button {
                    text = "${upgrade.title}: ${upgrade.description}"
                    onPressed += {
                        upgrade.collect()
                        onUpgradeSelect.emit()
                    }
                }
            }
        }
    }
}