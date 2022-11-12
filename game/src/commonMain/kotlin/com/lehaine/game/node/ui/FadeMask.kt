package com.lehaine.game.node.ui

import com.lehaine.game.Assets
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.node
import com.lehaine.littlekt.graph.node.ui.TextureRect
import com.lehaine.littlekt.graphics.Color
import com.lehaine.rune.engine.Cooldown
import kotlin.time.Duration

fun Node.fadeMask(delay: Duration, fadeTime: Duration) = node(FadeMask(delay, fadeTime))

/**
 * @author Colton Daily
 * @date 11/11/2022
 */
class FadeMask(delay: Duration, fadeTime: Duration) : TextureRect() {

    init {
        slice = Assets.atlas.getByPrefix("fxPixel").slice
        anchorRight = 1f
        anchorBottom = 1f
        stretchMode = StretchMode.SCALE

        mouseFilter = MouseFilter.IGNORE

        val fadeColor = Color.BLACK.toMutableColor()
        color = fadeColor
        val cd = Cooldown()

        onReady += {
            cd.timeout("delay", delay) { cd.timeout("fade", fadeTime) { destroy() } }
        }

        onUpdate += {
            cd.update(it)
            if (cd.has("fade")) {
                fadeColor.a = cd.ratio("fade")
            }
            color = fadeColor
        }
    }
}