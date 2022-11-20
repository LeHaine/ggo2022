package com.lehaine.game.node.ui

import com.lehaine.game.GameState
import com.lehaine.game.data.createArenaUpgrades
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.component.VAlign
import com.lehaine.littlekt.graph.node.node
import com.lehaine.littlekt.graph.node.ui.*
import com.lehaine.littlekt.util.signal

fun Node.upgradesDialog(state: GameState, callback: UpgradesDialog.() -> Unit = {}) =
    node(UpgradesDialog(state), callback)

/**
 * @author Colton Daily
 * @date 11/11/2022
 */
class UpgradesDialog(state: GameState) : Control() {

    private val arenaUpgrades = createArenaUpgrades(state)
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

    fun refresh(upgradeType: UpgradeType) {
        buttonColumn.destroyAllChildren()
        buttonColumn.apply {
            if (upgradeType == UpgradeType.ARENA) {
                repeat(3) {
                    val upgrade = arenaUpgrades.random()
                    soundButton {
                        text = "${upgrade.title}\n${upgrade.description}"
                        verticalAlign = VAlign.TOP
                        onPressed += {
                            upgrade.collect()
                            onUpgradeSelect.emit()
                        }
                    }
                }
            } else {
                // TODO display all upgrades for office
            }
        }
    }

    enum class UpgradeType {
        ARENA,
        OFFICE
    }
}