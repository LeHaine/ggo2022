package com.lehaine.game.node.entity

import com.lehaine.game.Assets
import com.lehaine.game.Config
import com.lehaine.littlekt.graphics.tilemap.ldtk.LDtkEntity
import com.lehaine.rune.engine.node.renderable.entity.Entity
import com.lehaine.rune.engine.node.renderable.entity.toGridPosition
import com.lehaine.rune.engine.node.renderable.sprite

/**
 * @author Colton Daily
 * @date 11/7/2022
 */
class BoneMan(data: LDtkEntity) : Entity(Config.GRID_CELL_SIZE.toFloat()) {

    init {
        toGridPosition(data.cx, data.cy)

        sprite {
            slice = Assets.atlas.getByPrefix("boneManDesk").slice
            anchorX = 0.5f
            anchorY = 1f
        }.also { sendChildToTop(it) }

        sprite.playLooped(Assets.boneManIdle)
    }

}