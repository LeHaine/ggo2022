package com.lehaine.game.node.ui

import com.lehaine.game.GameState
import com.lehaine.game.data.Upgrade
import com.lehaine.game.data.createArenaUpgrades
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.resource.HAlign
import com.lehaine.littlekt.graph.node.resource.VAlign
import com.lehaine.littlekt.graph.node.node
import com.lehaine.littlekt.graph.node.ui.*
import com.lehaine.littlekt.util.signal
import com.lehaine.rune.engine.Cooldown
import kotlin.time.Duration.Companion.seconds

fun Node.upgradesDialog(state: GameState, callback: UpgradesDialog.() -> Unit = {}) =
    node(UpgradesDialog(state), callback)

/**
 * @author Colton Daily
 * @date 11/11/2022
 */
class UpgradesDialog(state: GameState) : Control() {

    private val arenaUpgrades = createArenaUpgrades(state)
    private var buttonColumn: VBoxContainer
    private val currentUpgrades = mutableSetOf<Upgrade>()

    val onUpgradeSelect = signal()

    init {
        anchor(AnchorLayout.CENTER)

        centerContainer {
            anchor(AnchorLayout.CENTER)
            panelContainer {
                paddedContainer {
                    padding(10)
                    paddingTop = 5
                    column {
                        separation = 15
                        label {
                            text = "Punishment"
                            horizontalAlign = HAlign.CENTER
                            fontScaleX = 2f
                            fontScaleY = 2f
                        }
                        buttonColumn = column {
                            separation = 10
                        }
                    }
                }
            }
        }
    }

    fun refresh() {
        buttonColumn.destroyAllChildren()
        currentUpgrades.clear()
        buttonColumn.apply {
            while (currentUpgrades.size < 3) {
                val upgrade = arenaUpgrades.random()
                if (currentUpgrades.add(upgrade)) {
                    soundButton {
                        val cd = Cooldown()
                        text = "${upgrade.title}\n${upgrade.description}"
                        verticalAlign = VAlign.TOP
                        onReady += {
                            cd.timeout("delay", 1.seconds)
                        }
                        onPressed += {
                            if (!disabled) {
                                upgrade.collect()
                                onUpgradeSelect.emit()
                            }
                        }

                        onUpdate += {
                            cd.update(it)
                            disabled = cd.has("delay")
                        }
                    }
                }
            }
        }
    }
}