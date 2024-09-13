package dev.blackoutburst.mapeditor.window.callbacks

import dev.blackoutburst.mapeditor.input.Mouse
import org.lwjgl.glfw.GLFWMouseButtonCallbackI

class MouseButtonCallBack : GLFWMouseButtonCallbackI {
    override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
        Mouse.buttons[button] = action
    }
}
