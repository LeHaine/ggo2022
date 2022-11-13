package com.lehaine.game.node.ui

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.game.node.game
import com.lehaine.game.node.hero
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.annotation.SceneGraphDslMarker
import com.lehaine.littlekt.graph.node.component.AlignMode
import com.lehaine.littlekt.graph.node.node
import com.lehaine.littlekt.graph.node.ui.*
import com.lehaine.littlekt.graphics.Color
import com.lehaine.littlekt.graphics.TextureSlice
import com.lehaine.rune.engine.ActionCreator
import com.lehaine.rune.engine.actionCreator
import com.lehaine.rune.engine.node.renderable.entity.cd
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun Node.actionBar(
    callback: @SceneGraphDslMarker ActionBar.() -> Unit = {}
) = node(ActionBar(), callback)

/**
 * @author Colton Daily
 * @date 11/11/2022
 */
class ActionBar : Control() {

    private val _actionItems = mutableListOf<ActionBarItem>()
    val actionItems: List<ActionBarItem> get() = _actionItems

    init {
        anchorTop = 1f
        anchorRight = 0.5f
        anchorLeft = 0.5f
        anchorBottom = 1f
        marginTop = -40f
        marginLeft = -125f
        marginRight = -125f

        ninePatchRect {
            texture = Assets.atlas.getByPrefix("uiBarPanel").slice
            left = 15
            right = 15
            top = 14
            bottom = 14
            minWidth = 250f
        }


        val uiBarItem = Assets.atlas.getByPrefix("uiBarItem").slice
        row {
            separation = 10
            align = AlignMode.CENTER
            minWidth = 250f

            repeat(5) {
                textureRect {
                    slice = uiBarItem
                }
            }
        }

        row {
            separation = 10
            align = AlignMode.CENTER
            minWidth = 250f


            _actionItems += actionBarItem(
                unlockedIcon = Assets.atlas.getByPrefix("uiSwipeIcon").slice,
                keybindIcon = Assets.atlas.getByPrefix("uiLmbIcon").slice,
                itemName = "swipeCD"
            ) { true }

            _actionItems += actionBarItem(
                unlockedIcon = Assets.atlas.getByPrefix("uiSpiritOrbIcon").slice,
                keybindIcon = Assets.atlas.getByPrefix("uiRmbIcon").slice,
                itemName = "shootCD"
            ) { game.state.shootingUnlocked }

            _actionItems += actionBarItem(
                unlockedIcon = Assets.atlas.getByPrefix("uiDashIcon").slice,
                keybindIcon = Assets.atlas.getByPrefix("uiShiftSpaceIcon").slice,
                itemName = "dashCD"
            ) { game.state.dashUnlocked }

            _actionItems += actionBarItem(
                unlockedIcon = Assets.atlas.getByPrefix("uiBoneSpearIcon").slice,
                keybindIcon = Assets.atlas.getByPrefix("uiEIcon").slice,
                itemName = "boneSpearCD"
            ) { game.state.boneSpearUnlocked }

            _actionItems += actionBarItem(
                unlockedIcon = Assets.atlas.getByPrefix("uiHandOfDeathIcon").slice,
                keybindIcon = if (Config.keyboardType == Config.KeyboardType.QWERTY) {
                    Assets.atlas.getByPrefix("uiQIcon").slice
                } else {
                    Assets.atlas.getByPrefix(
                        "uiAIcon"
                    ).slice
                },
                itemName = "handOfDeathCD"
            ) { game.state.handOfDeathUnlocked }
        }
    }

}

private fun Node.actionBarItem(
    unlockedIcon: TextureSlice,
    keybindIcon: TextureSlice,
    itemName: String,
    isUnlocked: () -> Boolean,
) = node(ActionBarItem(unlockedIcon, keybindIcon, itemName, isUnlocked))

class ActionBarItem(
    private val unlockedIcon: TextureSlice,
    keybindIcon: TextureSlice,
    itemName: String,
    isUnlocked: () -> Boolean
) : TextureProgress() {

    private val lockedIcon = Assets.atlas.getByPrefix("uiLockedIcon").slice
    private val cooldownIcon = Assets.atlas.getByPrefix("uiCooldownBg").slice

    init {
        background = lockedIcon
        progressBar = cooldownIcon

        onUpdate += {
            ratio = hero.cd.ratio(itemName)

            background = if (isUnlocked()) {
                unlockedIcon
            } else {
                lockedIcon
            }
        }

        textureRect {
            slice = keybindIcon
            y -= slice?.height ?: 0
        }
    }
}