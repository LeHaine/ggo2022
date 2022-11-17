package com.lehaine.game.node.ui

import com.lehaine.game.Assets
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.annotation.SceneGraphDslMarker
import com.lehaine.littlekt.graph.node.ui.Button
import com.lehaine.littlekt.graph.node.ui.button

fun Node.soundButton(callback: @SceneGraphDslMarker Button.() -> Unit) = button {
    onFocus += {
        Assets.sfxSelect.play(0.1f)
    }

    callback()
}