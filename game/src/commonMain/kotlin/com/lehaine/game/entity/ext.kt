package com.lehaine.game.entity

import com.lehaine.game.Fx
import com.lehaine.game.scene.GameScene
import com.lehaine.littlekt.input.InputMapController
import com.lehaine.rune.engine.node.renderable.entity.Entity

val Entity.game: GameScene get() = scene!! as GameScene
val Entity.fx: Fx get() = game.fx

@Suppress("UNCHECKED_CAST")
val Entity.controller
    get() = scene!!.controller as InputMapController<String>