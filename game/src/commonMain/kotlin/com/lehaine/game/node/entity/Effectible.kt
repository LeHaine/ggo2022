package com.lehaine.game.node.entity

import com.lehaine.game.node.entity.mob.Effect
import com.lehaine.littlekt.util.fastForEach
import kotlin.time.Duration

/**
 * @author Colton Daily
 * @date 11/6/2022
 */
interface Effectible {
    val effects: MutableMap<Effect, Duration>
    val effectsToRemove: MutableList<Effect>

    fun isEffectible(): Boolean

    fun hasEffect(effect: Effect) = effects.contains(effect)
    fun getEffectDuration(effect: Effect) = effects[effect] ?: Duration.ZERO
    fun addEffect(effect: Effect, duration: Duration) {
        if (!isEffectible() || effects.contains(effect) && (effects[effect] ?: Duration.ZERO) > duration) {
            return
        }
        if (duration <= Duration.ZERO) {
            clearEffect(effect)
        } else {
            val isNew = hasEffect(effect)
            effects[effect] = duration
            if (isNew) {
                onEffectStart(effect)
            }
        }
    }

    fun clearEffect(effect: Effect) {
        effects.remove(effect)?.let { onEffectEnd(effect) }
    }

    fun onEffectStart(effect: Effect) = Unit

    fun onEffectEnd(effect: Effect) = Unit

    fun updateEffects(dt: Duration) {
        if (!isEffectible()) return
        effects.forEach { entry ->
            val remainingDuration = entry.value - dt
            if (remainingDuration <= Duration.ZERO) {
                effectsToRemove += entry.key
            } else {
                effects[entry.key] = remainingDuration
            }
        }
        effectsToRemove.fastForEach {
            clearEffect(it)
        }
        effectsToRemove.clear()
    }

}