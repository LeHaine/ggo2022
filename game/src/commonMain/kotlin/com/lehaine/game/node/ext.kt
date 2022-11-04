package com.lehaine.game.node

import com.lehaine.game.Fx
import com.lehaine.game.GameInput
import com.lehaine.game.node.entity.Hero
import com.lehaine.game.scene.GameScene
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.input.InputMapController
import com.lehaine.rune.engine.node.renderable.entity.Entity

val Node.game: GameScene get() = scene!! as GameScene
val Node.fx: Fx get() = game.fx

val Node.hero: Hero get() = game.hero

@Suppress("UNCHECKED_CAST")
val Entity.controller
    get() = scene!!.controller as InputMapController<GameInput>