package dev.blackoutburst.mapeditor

import dev.blackoutburst.mapeditor.window.Window
import java.util.concurrent.ConcurrentLinkedQueue

object Main {
    val queue: ConcurrentLinkedQueue<() -> Unit> = ConcurrentLinkedQueue()
}

fun main() {
    Window

    update()
}

fun update() {
    while (Window.isOpen) {
        Window.clear()

        Window.update()
    }
}