package dev.blackoutburst.sme.graphics

import dev.blackoutburst.sme.camera.Camera
import dev.blackoutburst.sme.graphics.framebuffer.Framebuffer
import dev.blackoutburst.sme.graphics.texture.TextureArray
import dev.blackoutburst.sme.maths.Matrix
import dev.blackoutburst.sme.maths.Vector3f
import dev.blackoutburst.sme.shader.Shader
import dev.blackoutburst.sme.shader.ShaderProgram
import dev.blackoutburst.sme.utils.Color
import dev.blackoutburst.sme.utils.stack
import dev.blackoutburst.sme.window.Window
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*

object World {
    private val worldVertexShader = Shader(GL_VERTEX_SHADER, "/shaders/world.vert")
    private val worldFragmentShader = Shader(GL_FRAGMENT_SHADER, "/shaders/world.frag")
    private val worldShaderProgram = ShaderProgram(worldVertexShader, worldFragmentShader)

    private val shadowFragmentShader = Shader(GL_FRAGMENT_SHADER, "/shaders/empty.frag")
    private val shadowShaderProgram = ShaderProgram(worldVertexShader, shadowFragmentShader)

    private val sveVertexShader = Shader(GL_VERTEX_SHADER, "/shaders/sve.vert")
    private val sveFragmentShader = Shader(GL_FRAGMENT_SHADER, "/shaders/sve.frag")
    private val sveShaderProgram = ShaderProgram(sveVertexShader, sveFragmentShader)

    val cubes = mutableListOf<Cube>()

    private val shadowMap = Framebuffer(4096 * 2, 4096 * 2, true)

    var textures = TextureArray(listOf("cube.png"), 48, 32)

    private var vaoId = 0
    private var vboId = 0
    private var eboId = 0

    private var vertices: FloatArray? = null
    private var indices: IntArray? = null
    private var indexCount = 0

    init {
        vaoId = glGenVertexArrays()
        vboId = glGenBuffers()
        eboId = glGenBuffers()

        updateWorld()
    }

    private fun generateVAO() {
        stack(500 * 1000000) { stack ->
            glBindVertexArray(vaoId)

            // VBO
            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            val vertexBuffer = stack.mallocFloat(vertices?.size ?: 0)
            vertexBuffer.put(vertices).flip()
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

            // EBO
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
            val indexBuffer = stack.mallocInt(indices?.size ?: 0)
            indexBuffer.put(indices).flip()
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW)

            // ATTRIB
            // POSITION
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 28, 0)

            // UV
            glEnableVertexAttribArray(1)
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 28, 12)

            // NORMAL
            glEnableVertexAttribArray(2)
            glVertexAttribPointer(2, 1, GL_FLOAT, false, 28, 20)

            // LAYER
            glEnableVertexAttribArray(3)
            glVertexAttribPointer(3, 1, GL_FLOAT, false, 28, 24)
        }

        indexCount = indices?.size ?: 0

        vertices = null
        indices = null
    }

    private fun calculateVertexArray() {
        val vertexArray = mutableListOf<Float>()
        cubes.forEach {
            // TOP
            vertexArray.addAll(listOf(it.position.x, it.position.y + 1, it.position.z,         0f / 3f, 0f / 2f, 0f, 0f))
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y + 1, it.position.z,     1f / 3f, 0f / 2f, 0f, 0f))
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y + 1, it.position.z + 1, 1f / 3f, 1f / 2f, 0f, 0f))
            vertexArray.addAll(listOf(it.position.x, it.position.y + 1, it.position.z + 1,     0f / 3f, 1f / 2f, 0f, 0f))

            // BACK
            vertexArray.addAll(listOf(it.position.x, it.position.y, it.position.z,             2f / 3f, 2f / 2f, 1f, 0f))
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y, it.position.z,         1f / 3f, 2f / 2f, 1f, 0f))
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y + 1, it.position.z,     1f / 3f, 1f / 2f, 1f, 0f))
            vertexArray.addAll(listOf(it.position.x, it.position.y + 1, it.position.z,         2f / 3f, 1f / 2f, 1f, 0f))

            // FRONT
            vertexArray.addAll(listOf(it.position.x, it.position.y, it.position.z + 1,         1f / 3f, 1f / 2f, 2f, 0f))
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y, it.position.z + 1,     2f / 3f, 1f / 2f, 2f, 0f))
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y + 1, it.position.z + 1, 2f / 3f, 0f / 2f, 2f, 0f))
            vertexArray.addAll(listOf(it.position.x, it.position.y + 1, it.position.z + 1,     1f / 3f, 0f / 2f, 2f, 0f))

            // LEFT
            vertexArray.addAll(listOf(it.position.x, it.position.y, it.position.z,             2f / 3f, 1f / 2f, 3f, 0f))
            vertexArray.addAll(listOf(it.position.x, it.position.y + 1, it.position.z,         2f / 3f, 0f / 2f, 3f, 0f))
            vertexArray.addAll(listOf(it.position.x, it.position.y + 1, it.position.z + 1,     3f / 3f, 0f / 2f, 3f, 0f))
            vertexArray.addAll(listOf(it.position.x, it.position.y, it.position.z + 1,         3f / 3f, 1f / 2f, 3f, 0f))

            // RIGHT
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y, it.position.z,         3f / 3f, 2f / 2f, 4f, 0f))
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y + 1, it.position.z,     3f / 3f, 1f / 2f, 4f, 0f))
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y + 1, it.position.z + 1, 2f / 3f, 1f / 2f, 4f, 0f))
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y, it.position.z + 1,     2f / 3f, 2f / 2f, 4f, 0f))

            // BOTTOM
            vertexArray.addAll(listOf(it.position.x, it.position.y, it.position.z,             1f / 3f, 1f / 2f, 5f, 0f))
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y, it.position.z,         0f / 3f, 1f / 2f, 5f, 0f))
            vertexArray.addAll(listOf(it.position.x + 1, it.position.y, it.position.z + 1,     0f / 3f, 2f / 2f, 5f, 0f))
            vertexArray.addAll(listOf(it.position.x, it.position.y, it.position.z + 1,         1f / 3f, 2f / 2f, 5f, 0f))
        }

        vertices = vertexArray.toFloatArray()
    }

    fun calculateIndexArray() {
        val indexArray = mutableListOf<Int>()
        var offset = 0
        cubes.forEach {
            indexArray.addAll(
                listOf(
                    // TOP
                    0 + offset, 2 + offset, 1 + offset,
                    0 + offset, 3 + offset, 2 + offset,

                    // FRONT
                    4 + offset, 6 + offset, 5 + offset,
                    4 + offset, 7 + offset, 6 + offset,

                    // BACK
                    9 + offset, 10 + offset, 8 + offset,
                    10 + offset, 11 + offset, 8 + offset,

                    // LEFT
                    12 + offset, 14 + offset, 13 + offset,
                    12 + offset, 15 + offset, 14 + offset,

                    // RIGHT
                    17 + offset, 18 + offset, 16 + offset,
                    18 + offset, 19 + offset, 16 + offset,

                    // BOTTOM
                    21 + offset, 22 + offset, 20 + offset,
                    22 + offset, 23 + offset, 20 + offset,
                )
            )
            offset += 24
        }

        indices = indexArray.toIntArray()
    }

    fun updateWorld() {
        calculateVertexArray()
        calculateIndexArray()
        generateVAO()
    }

    fun addCubes(cubes: List<Cube>) {
        for (cube in cubes) {
            if (getVoxelByPosition(cube.position) != null) continue
            this.cubes.add(cube)
        }

        updateWorld()
    }

    fun removeCubes(cubes: List<Cube>) {
        for (cube in cubes) {
            this.cubes.remove(cube)
        }
        updateWorld()
    }

    fun getVoxelByPosition(position: Vector3f): Cube? = cubes.firstOrNull { it.position.x == position.x && it.position.y == position.y && it.position.z == position.z }

    fun render() {
        shadowMapCreation()

        glBindVertexArray(vaoId)
        glUseProgram(worldShaderProgram.id)
        glEnable(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D_ARRAY, textures.id)
        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, shadowMap.texture)

        worldShaderProgram.setUniform1i("shadowMap", 1)
        worldShaderProgram.setUniform1i("diffuseMap", 0)
        worldShaderProgram.setUniform3f("lightColor", Color.WHITE)

        worldShaderProgram.setUniform3f("viewPos", Camera.position)
        worldShaderProgram.setUniformMat4("view", Camera.view)
        worldShaderProgram.setUniformMat4("projection", Camera.projection)

        val a = 100f
        worldShaderProgram.setUniformMat4("lightProjection", Matrix()
            .ortho2D(-a, a, -a, a, -20f - Camera.position.z, 20f - Camera.position.z))

        worldShaderProgram.setUniformMat4("lightView", Matrix()
            .rotate(Math.toRadians(70.0).toFloat(), Vector3f(1f, 0f, 0f))
            .rotate(Math.toRadians(-120.0).toFloat(), Vector3f(0f, 1f, 0f))
            .translate(Vector3f(-Camera.position.x + Camera.positionOffset.x, 0f, Camera.positionOffset.z))
        )

        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0)
    }

    private fun shadowMapCreation() {
        glUseProgram(shadowShaderProgram.id)

        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.fbo)
        glViewport(0, 0, shadowMap.width, shadowMap.height)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val a = 100f
        val view = Matrix()
            .rotate(Math.toRadians(70.0).toFloat(), Vector3f(1f, 0f, 0f))
            .rotate(Math.toRadians(-120.0).toFloat(), Vector3f(0f, 1f, 0f))
            .translate(Vector3f(-Camera.position.x + Camera.positionOffset.x, 0f, Camera.positionOffset.z))

        val projection = Matrix()
            .ortho2D(-a, a, -a, a, -20f - Camera.position.z, 20f - Camera.position.z)


        shadowShaderProgram.setUniformMat4("view", view)
        shadowShaderProgram.setUniformMat4("projection", projection)

        glDisable(GL_CULL_FACE)

        glBindVertexArray(vaoId)
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0)
        //EntityManager.render(view, projection)

        glEnable(GL_CULL_FACE)

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glViewport(0, 0, Window.width, Window.height)
    }
}