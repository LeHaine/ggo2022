package com.lehaine.game.node

import com.lehaine.game.node.entity.mob.Mob
import com.lehaine.littlekt.graph.node.Node
import com.lehaine.littlekt.graph.node.addTo
import com.lehaine.littlekt.graph.node.annotation.SceneGraphDslMarker
import com.lehaine.littlekt.util.fastForEach
import com.lehaine.rune.engine.Cooldown
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration

@OptIn(ExperimentalContracts::class)
fun Node.monsterSpawner(callback: @SceneGraphDslMarker MonsterSpawner.() -> Unit = {}): MonsterSpawner {
    contract { callsInPlace(callback, InvocationKind.EXACTLY_ONCE) }
    return MonsterSpawner().also(callback).addTo(this)
}

/**
 * @author Colton Daily
 * @date 7/26/2022
 */
open class MonsterSpawner : Node() {

    private val events = mutableListOf<Event>()
    private var timeElapsed = Duration.ZERO
    private val cd = Cooldown()

    data class Event(
        val startAt: Duration,
        val endAt: Duration,
        val oneTime: Boolean,
        var actionCondition: () -> Boolean,
        var actionTimer: Duration,
        val action: (() -> Unit)?,
        val onFinish: (() -> Unit)?,
        var finished: Boolean = false
    )

    class EventBuilder {
        var startAt: Duration = Duration.ZERO
        var endAt: Duration = Duration.INFINITE
        var oneTime: Boolean = true
        var actionCondition: () -> Boolean = { true }
        var action: (() -> Unit)? = null
        var actionTimer = Duration.ZERO
        var onFinish: (() -> Unit)? = null

        fun build() = Event(
            startAt = startAt,
            endAt = endAt,
            oneTime = oneTime,
            actionCondition = actionCondition,
            actionTimer = actionTimer,
            action = action,
            onFinish = onFinish
        )
    }

    fun addEvent(buildEvent: EventBuilder.() -> Unit) {
        val builder = EventBuilder()
        builder.apply(buildEvent)
        events += builder.build()
    }

    override fun update(dt: Duration) {
        super.update(dt)
        cd.update(dt)
        timeElapsed += dt
        events.fastForEach { event ->
            while (!event.finished
                && event.startAt <= timeElapsed
                && timeElapsed < event.endAt
                && !cd.has("actionTimer")
                && event.action != null && event.actionCondition()
            ) {
                event.action.invoke().also {
                    if (event.actionTimer > Duration.ZERO) {
                        cd.timeout("actionTimer", event.actionTimer)
                    }
                }

                if (event.oneTime) {
                    event.finished = true
                    event.onFinish?.invoke()
                }
            }
            if (timeElapsed >= event.endAt && !event.finished) {
                event.finished = true
                event.onFinish?.invoke()
            }
        }
        events.removeAll { it.finished }
    }

    fun spawnMob(mob: Mob) {
        mob.spawn()
        mob.addTo(game.entities)
    }
}