package com.lehaine.game

import com.lehaine.littlekt.graphics.tilemap.ldtk.LDtkLevel
import com.lehaine.rune.engine.node.renderable.LDtkGameLevelRenderable

class Level(level: LDtkLevel) : LDtkGameLevelRenderable<Level.LevelMark>(level) {
    override var gridSize: Int = 16

    enum class LevelMark {

    }
}