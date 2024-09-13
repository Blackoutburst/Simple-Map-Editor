package dev.blackoutburst.mapeditor.window.callbacks

import dev.blackoutburst.mapeditor.input.Keyboard
import org.lwjgl.glfw.GLFWKeyCallbackI

class KeyboardCallBack : GLFWKeyCallbackI {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        Keyboard.keys[key] = action
    }
}
