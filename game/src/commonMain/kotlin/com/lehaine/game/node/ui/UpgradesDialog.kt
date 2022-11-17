package com.lehaine.game.node.ui

import com.lehaine.game.GameState
import com.lehaine.game.data.createUpgrades
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
    private var buttonColumn: VBoxContainer

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
        buttonColumn.destroyAllChildren()
        buttonColumn.apply {
            repeat(3) {
                val upgrade = upgrades.random()
                soundButton {
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