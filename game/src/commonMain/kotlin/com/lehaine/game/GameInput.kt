package com.lehaine.game

import com.lehaine.littlekt.graph.SceneGraph

enum class GameInput {
    UI_ACCEPT,
    UI_SELECT,
    UI_CANCEL,
    UI_UP,
    UI_DOWN,
    UI_LEFT,
    UI_RIGHT,
    UI_HOME,
    UI_END,
    UI_FOCUS_NEXT,
    UI_FOCUS_PREV,

    SHOOT,
    SWING,
    DASH,
    HAND_OF_DEATH,
    BONE_SPEAR,

    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_UP,
    MOVE_DOWN,

    HORIZONTAL,
    VERTICAL,

    MOVEMENT
}

fun createUiGameInputSignals() = SceneGraph.UiInputSignals(
    GameInput.UI_ACCEPT,
    GameInput.UI_SELECT,
    GameInput.UI_CANCEL,
    GameInput.UI_FOCUS_NEXT,
    GameInput.UI_FOCUS_PREV,
    GameInput.UI_LEFT,
    GameInput.UI_RIGHT,
    GameInput.UI_UP,
    GameInput.UI_DOWN,
    GameInput.UI_HOME,
    GameInput.UI_END
)