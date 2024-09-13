package dev.blackoutburst.mapeditor.camera

import dev.blackoutburst.mapeditor.input.Keyboard
import dev.blackoutburst.mapeditor.input.Mouse
import dev.blackoutburst.mapeditor.maths.Matrix
import dev.blackoutburst.mapeditor.maths.Vector2f
import dev.blackoutburst.mapeditor.maths.Vector3f
import dev.blackoutburst.mapeditor.maths.Vector4f
import dev.blackoutburst.mapeditor.window.Window
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin

object Camera {
    private var lastMousePosition = Mouse.position
    private val sensitivity = 0.2f

    var position = Vector3f(0f, 0f, 5f)

    var positionOffset = Vector3f(0f, 0f, 0f)

    var rotation = Vector2f(40f, 30f)

    var view = Matrix().translate(position)
    var projection = Matrix().projectionMatrix(90f, 1000f, 0.1f)
    var projection2D = Matrix().ortho2D(0f, Window.width.toFloat(), 0f, Window.height.toFloat(), -1f, 1f)

    val ray: Vector3f
        get() {
            val mouseXNDC = (2.0 * Mouse.position.x / Window.width) - 1.0
            val mouseYNDC = 1.0 - (2.0 * Mouse.position.y / Window.height)
            val rayClip = Vector4f(mouseXNDC.toFloat(), mouseYNDC.toFloat(), -1.0f, 1.0f)

            val inverseProjection = projection.copy().invert()
            val inverseView = view.copy().invert()

            val rayEye = inverseProjection.transform(rayClip)
            rayEye.z = -1.0f
            rayEye.w = 0.0f

            val rayWorld = inverseView.transform(Vector4f(rayEye.x, rayEye.y, rayEye.z, rayEye.w))

            return Vector3f(rayWorld.x, rayWorld.y, rayWorld.z).normalize()
        }

    private fun getSpacePosition(): Vector3f {
        val inverseView = view.copy().invert()
        return Vector3f(inverseView.m30, inverseView.m31, inverseView.m32)
    }

    fun update() {
        if (Keyboard.isKeyPressed(GLFW.GLFW_KEY_R)) {
            position.set(0f, 0f, 5f)
            positionOffset.set(0f, 0f, 0f)
            rotation.set(40f, 30f)
        }

        val mousePosition = Mouse.position

        var xOffset = mousePosition.x - lastMousePosition.x
        var yOffset = mousePosition.y - lastMousePosition.y
        xOffset *= sensitivity
        yOffset *= sensitivity

        lastMousePosition = mousePosition.copy()


        position.z -= Mouse.scroll / 2f
        if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            rotate(xOffset, yOffset)
            move(xOffset, yOffset)
        }

        view.setIdentity()
            .translate(0f, 0f, -position.z)
            .rotate(Math.toRadians(rotation.y.toDouble()).toFloat(), 1f, 0f, 0f)
            .rotate(Math.toRadians(rotation.x.toDouble()).toFloat(), 0f, 1f, 0f)
            .translate(-position.x + positionOffset.x, -position.y + positionOffset.y, positionOffset.z)
    }

    private fun rotate(xOffset: Float, yOffset: Float) {
        if (!Mouse.isButtonDown(Mouse.LEFT_BUTTON)) return

        rotation.x += xOffset
        rotation.y += yOffset

        if (rotation.y > 90.0f) rotation.y = 90.0f
        if (rotation.y < -90.0f) rotation.y = -90.0f
    }

    private fun move(xOffset: Float, yOffset: Float) {
        if (!Mouse.isButtonDown(Mouse.RIGHT_BUTTON)) return

        positionOffset.x += cos(-rotation.x * Math.PI / 180).toFloat() * xOffset / 20f
        positionOffset.z -= sin(-rotation.x * Math.PI / 180).toFloat() * xOffset / 20f

        positionOffset.y -= yOffset / 20f

    }
}
