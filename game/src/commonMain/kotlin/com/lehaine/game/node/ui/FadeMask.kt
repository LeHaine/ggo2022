package com.lehaine.game.node.ui

import com.lehaine.game.Assets
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.node
import com.lehaine.littlekt.graph.node.ui.TextureRect
import com.lehaine.littlekt.graphics.Color
import com.lehaine.littlekt.util.signal
import com.lehaine.rune.engine.Cooldown
import kotlin.time.Duration

fun Node.fadeMask(
    delay: Duration,
    fadeTime: Duration,
    fadeColor: Color = Color.fromHex("#332e30"),
    fade: FadeMask.Fade = FadeMask.Fade.OUT,
    callback: FadeMask.() -> Unit = {}
) = node(FadeMask(delay, fadeTime, fadeColor, fade), callback)

/**
 * @author Colton Daily
 * @date 11/11/2022
 */
class FadeMask(delay: Duration, fadeTime: Duration, fadeColor: Color = Color.fromHex("#332e30"), fade: Fade = Fade.OUT) :
    TextureRect() {

    val onFinish = signal()

    init {
        slice = Assets.atlas.getByPrefix("fxPixel").slice
        anchorRight = 1f
        anchorBottom = 1f
        stretchMode = StretchMode.SCALE

        mouseFilter = MouseFilter.IGNORE

        val mFadeColor = fadeColor.toMutableColor()
        if (fade == Fade.IN) {
            mFadeColor.a = 0f
        }
        color = mFadeColor
        val cd = Cooldown()

        onReady += {
            cd.timeout("delay", delay) { cd.timeout("fade", fadeTime) { onFinish.emit() } }
        }

        onUpdate += {
            cd.update(it)
            if (cd.has("fade")) {
                mFadeColor.a = if (fade == Fade.IN) 1 - cd.ratio("fade") else cd.ratio("fade")
            }
            color = mFadeColor
        }
    }

    enum class Fade {
        IN,
        OUT
    }
}

