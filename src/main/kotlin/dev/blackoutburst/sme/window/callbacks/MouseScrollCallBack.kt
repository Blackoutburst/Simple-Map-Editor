package dev.blackoutburst.sme.window.callbacks

import dev.blackoutburst.sme.input.Mouse
import org.lwjgl.glfw.GLFWScrollCallbackI

class MouseScrollCallBack : GLFWScrollCallbackI {
    override fun invoke(window: Long, xOffset: Double, yOffset: Double) {
        Mouse.scroll = (Mouse.scroll + yOffset).toFloat()
    }
}
