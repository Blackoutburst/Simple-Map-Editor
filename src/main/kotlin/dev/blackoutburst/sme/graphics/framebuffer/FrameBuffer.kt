package dev.blackoutburst.sme.graphics.framebuffer

import dev.blackoutburst.sme.maths.Matrix
import dev.blackoutburst.sme.maths.Vector2f
import dev.blackoutburst.sme.shader.Shader
import dev.blackoutburst.sme.shader.ShaderProgram
import dev.blackoutburst.sme.utils.stack
import dev.blackoutburst.sme.window.Window
import org.lwjgl.opengl.GL30.*

class Framebuffer(val width: Int, val height: Int, depthBuffer: Boolean = false) {
    var fbo = glGenFramebuffers()
    var rbo = glGenRenderbuffers()
    var texture = glGenTextures()

    private val vaoID = glGenVertexArrays()
    private val vboID = glGenBuffers()
    private val eboID = glGenBuffers()

    private val vertexShader = Shader(GL_VERTEX_SHADER, "/shaders/2D.vert")
    private val fragmentShader = Shader(GL_FRAGMENT_SHADER, if (depthBuffer) "/shaders/depth.frag" else "/shaders/2D.frag")
    private val shaderProgram = ShaderProgram(vertexShader, fragmentShader)

    private val vertices = floatArrayOf(
        0f, 0f, 0f, 0f,
        1f, 1f, 1f, 1f,
        0f, 1f, 0f, 1f,
        1f, 0f, 1f, 0f,
    )

    private val indices = intArrayOf(
        0, 1, 2,
        0, 3, 1,
    )

    init {
        if (depthBuffer)
            genDepthBuffers()
        else
            genBuffers()
        genVAO()
    }

    private fun genVAO() {
        stack { stack ->

            glBindVertexArray(vaoID)

            glBindBuffer(GL_ARRAY_BUFFER, vboID)
            val vertexBuffer = stack.mallocFloat(vertices.size)
            vertexBuffer.put(vertices).flip()
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID)
            val indexBuffer = stack.mallocInt(indices.size)
            indexBuffer.put(indices).flip()
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW)

            glEnableVertexAttribArray(0)
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 16, 0)
            glEnableVertexAttribArray(1)
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 16, 8)

            glBindVertexArray(0)
        }
    }

    private fun genBuffers() {
        glBindTexture(GL_TEXTURE_2D, texture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0)

        glBindRenderbuffer(GL_RENDERBUFFER, rbo)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo)

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            println("Error: Framebuffer is not complete!")
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    private fun genDepthBuffers() {
        glBindTexture(GL_TEXTURE_2D, texture)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
        val borderColor = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor)


        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, texture, 0)

        glDrawBuffer(GL_NONE)
        glReadBuffer(GL_NONE)

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            println("Error: Framebuffer is not complete!")
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun render(x: Float, y: Float, width: Float, height: Float) {
        glDisable(GL_DEPTH_TEST)

        glUseProgram(shaderProgram.id)
        shaderProgram.setUniform1i("diffuseMap", 0)
        shaderProgram.setUniformMat4("model", Matrix().translate(Vector2f(x, y)).scale(Vector2f(width, height)))
        shaderProgram.setUniformMat4("projection", Matrix().ortho2D(0f, Window.width.toFloat(), 0f, Window.height.toFloat(), -1f, 1f))

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texture)

        glBindVertexArray(vaoID)
        glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)

        glEnable(GL_DEPTH_TEST)
    }
}