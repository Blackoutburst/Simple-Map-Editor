package dev.blackoutburst.sme.window.callbacks

import dev.blackoutburst.sme.input.Keyboard
import org.lwjgl.glfw.GLFWKeyCallbackI

class KeyboardCallBack : GLFWKeyCallbackI {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        Keyboard.keys[key] = action
    }
}
