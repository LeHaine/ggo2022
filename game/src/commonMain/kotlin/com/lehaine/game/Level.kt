package com.lehaine.game

import com.lehaine.littlekt.graphics.g2d.tilemap.ldtk.LDtkLevel
import com.lehaine.rune.engine.node.renderable.LDtkGameLevelRenderable

class Level(level: LDtkLevel) : LDtkGameLevelRenderable<Level.LevelMark>(level) {
    override var gridSize: Int = Config.GRID_CELL_SIZE

    override val collisionValues: IntArray = intArrayOf(1, 2, 3)


    enum class LevelMark {

    }
}