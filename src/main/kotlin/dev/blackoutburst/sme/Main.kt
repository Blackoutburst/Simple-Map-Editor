package dev.blackoutburst.sme

import dev.blackoutburst.sme.camera.Camera
import dev.blackoutburst.sme.files.FileExplorer
import dev.blackoutburst.sme.files.FileSystem
import dev.blackoutburst.sme.graphics.Axys
import dev.blackoutburst.sme.graphics.Cube
import dev.blackoutburst.sme.graphics.Grid
import dev.blackoutburst.sme.graphics.World
import dev.blackoutburst.sme.maths.Vector3f
import dev.blackoutburst.sme.window.Window
import java.util.concurrent.ConcurrentLinkedQueue
import org.lwjgl.opengl.GL11.*

object Main {
    val queue: ConcurrentLinkedQueue<() -> Unit> = ConcurrentLinkedQueue()
}

fun main() {
    FileSystem
    FileExplorer.init()
    Window

    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glEnable(GL_BLEND)

    update()
}

fun update() {
    World.addCubes(listOf(Cube(Vector3f(), 0)))
    World.addCubes(listOf(Cube(Vector3f(-1f, 0f, 0f), 0)))
    World.addCubes(listOf(Cube(Vector3f(-1f, 0f, -1f), 0)))
    World.addCubes(listOf(Cube(Vector3f(0f, 0f, -1f), 0)))

    while (Window.isOpen) {
        Window.clear()

        Axys.update()
        Grid.update()
        Camera.update()

        glEnable(GL_DEPTH_TEST)

        Axys.render()
        Grid.render()

        World.render()

        glDisable(GL_DEPTH_TEST)

        Window.update()
    }
}