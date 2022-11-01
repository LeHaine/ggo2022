package com.lehaine.game

import com.lehaine.rune.engine.Rune
import com.lehaine.game.scene.MenuScene
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.async.KtScope
import com.lehaine.littlekt.graphics.Color
import com.lehaine.littlekt.graphics.gl.ClearBufferMask
import com.lehaine.littlekt.input.Key
import com.lehaine.littlekt.log.Logger
import kotlinx.coroutines.launch

class GameCore(context: Context) : Rune(context) {


    init {
        Logger.setLevels(Logger.Level.DEBUG)
    }


    override suspend fun Context.create() {
        Assets.createInstance(this) {
            scene = MenuScene(context)
        }

        onRender {
            if (input.isKeyJustPressed(Key.ESCAPE)) {
                close()
            }
        }

        onPostRender {
            if (input.isKeyJustPressed(Key.P)) {
                logger.info { stats }
            }
        }

        onDispose {
            Assets.dispose()
        }
    }
}