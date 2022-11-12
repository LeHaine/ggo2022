package com.lehaine.game

import com.lehaine.game.scene.KeyboardSelectScene
import com.lehaine.littlekt.Context
import com.lehaine.littlekt.input.Key
import com.lehaine.littlekt.log.Logger
import com.lehaine.rune.engine.Rune

class GameCore(context: Context) : Rune(context) {


    init {
        Logger.setLevels(Logger.Level.DEBUG)
    }


    override suspend fun Context.create() {
        Assets.createInstance(this) {
            scene = KeyboardSelectScene(context)
        }

        onRender {
            if (platform == Context.Platform.DESKTOP && input.isKeyPressed(Key.CTRL_LEFT) && input.isKeyJustPressed(Key.W)) {
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