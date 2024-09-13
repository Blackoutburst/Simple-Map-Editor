package dev.blackoutburst.mapeditor.window.callbacks

import dev.blackoutburst.mapeditor.input.Mouse
import org.lwjgl.glfw.GLFWScrollCallbackI

class MouseScrollCallBack : GLFWScrollCallbackI {
    override fun invoke(window: Long, xOffset: Double, yOffset: Double) {
        Mouse.scroll = (Mouse.scroll + yOffset).toFloat()
    }
}
