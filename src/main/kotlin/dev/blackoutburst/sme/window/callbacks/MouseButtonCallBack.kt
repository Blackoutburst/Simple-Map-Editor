package dev.blackoutburst.sme.window.callbacks

import dev.blackoutburst.sme.input.Mouse
import org.lwjgl.glfw.GLFWMouseButtonCallbackI

class MouseButtonCallBack : GLFWMouseButtonCallbackI {
    override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
        Mouse.buttons[button] = action
    }
}
