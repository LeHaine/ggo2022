package com.lehaine.game.scene

import com.lehaine.game.Config
import com.lehaine.game.node.ui.fadeMask
import com.lehaine.game.node.ui.settingsDialog
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.graph.node.Node
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
        settingsDialog {
            onBack += {
                if (!switchingScenes) {
                    switchingScenes = true
                    changeTo(MenuScene(context))
                }
            }
        }
        fadeMask(delay = 250.milliseconds, fadeTime = 1.seconds)
    }
}