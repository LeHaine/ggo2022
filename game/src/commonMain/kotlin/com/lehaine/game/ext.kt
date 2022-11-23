/**
 * @author Colton Daily
 * @date 11/21/2022
 */
package com.lehaine.game

import com.lehaine.littlekt.math.random
import kotlin.random.Random


fun Float.about(variance: Float = 0.1f, sign: Boolean = false): Float {
    return this * (1 + (0..(variance * 100).toInt() / 100).random()) * (if (sign) randomSign else 1)
}

fun Int.about(variance: Float = 0.1f, sign: Boolean = false): Float {
    return about(this.toFloat(), sign)
}

fun <T> pickOne(one: T, two: T): T {
    val r = Random.nextFloat()
    if (r < 0.5f) return one
    return two
}

fun <T> pickOne(one: T, two: T, three: T): T {
    val r = Random.nextInt(3)
    if (r == 0) return one
    if (r == 1) return two
    return three
}

fun <T> pickOne(one: T, two: T, three: T, four: T): T {
    val r = Random.nextInt(4)
    if (r == 0) return one
    if (r == 1) return two
    if (r == 2) return three
    return four
}

fun <T> pickOne(one: T, two: T, three: T, four: T, five: T): T {
    val r = Random.nextInt(5)
    if (r == 0) return one
    if (r == 1) return two
    if (r == 2) return three
    if (r == 3) return four
    return five
}

val randomSign: Int get() = (0..1).random().toInt() * 2 - 1
val Float.asRandomSign: Float get() = if (Random.nextFloat() >= 0.5f) this else -this
val Int.asRandomSign: Int get() = if (Random.nextFloat() >= 0.5f) this else -this
