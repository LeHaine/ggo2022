package com.lehaine.game

import com.lehaine.littlekt.Context
import com.lehaine.littlekt.ContextListener
import com.lehaine.littlekt.LittleKtActivity
import com.lehaine.littlekt.LittleKtProps
import com.lehaine.littlekt.graphics.Color

class AppActivity : LittleKtActivity() {

    override fun LittleKtProps.configureLittleKt() {
        activity = this@AppActivity
        backgroundColor = Color.DARK_GRAY
    }

    override fun createContextListener(context: Context): ContextListener {
        return GameCore(context)
    }
}