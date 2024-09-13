package dev.blackoutburst.mapeditor

import dev.blackoutburst.mapeditor.camera.Camera
import dev.blackoutburst.mapeditor.files.FileExplorer
import dev.blackoutburst.mapeditor.graphics.Axys
import dev.blackoutburst.mapeditor.graphics.Grid
import dev.blackoutburst.mapeditor.window.Window
import java.util.concurrent.ConcurrentLinkedQueue
import org.lwjgl.opengl.GL11.*

object Main {
    val queue: ConcurrentLinkedQueue<() -> Unit> = ConcurrentLinkedQueue()
}

fun main() {
    FileExplorer.init()
    Window

    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glEnable(GL_BLEND)

    update()
}

fun update() {
    while (Window.isOpen) {
        Window.clear()

        Axys.update()
        Grid.update()
        Camera.update()

        glEnable(GL_DEPTH_TEST)

        Axys.render()
        Grid.render()

        glDisable(GL_DEPTH_TEST)

        Window.update()
    }
}