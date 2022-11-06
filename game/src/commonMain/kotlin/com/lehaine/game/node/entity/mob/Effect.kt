package com.lehaine.game.node.entity.mob

/**
 * @author Colton Daily
 * @date 11/6/2022
 */
sealed class Effect {

    object Stun : Effect()
    object Invincible : Effect()
}