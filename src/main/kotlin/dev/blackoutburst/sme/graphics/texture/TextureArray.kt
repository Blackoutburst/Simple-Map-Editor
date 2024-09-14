package dev.blackoutburst.sme.graphics.texture

import dev.blackoutburst.sme.utils.IOUtils
import dev.blackoutburst.sme.utils.stack
import org.lwjgl.opengl.GL30.*
import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer

class TextureArray(files: List<String>, width: Int = 16, height: Int = 16) {
    var id = -1

    init {
        stack { stack ->
            val widthBuffer = stack.mallocInt(1)
            val heightBuffer = stack.mallocInt(1)
            val comp = stack.mallocInt(1)

            id = glGenTextures()
            glBindTexture(GL_TEXTURE_2D_ARRAY, id)
            glTexImage3D(
                GL_TEXTURE_2D_ARRAY, 0, GL_RGBA8,
                width, height, files.size, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, null as ByteBuffer?
            )

            for ((index, file) in files.withIndex()) {
                val data = STBImage.stbi_load_from_memory(IOUtils.ioResourceToByteBuffer(file, 1024), widthBuffer, heightBuffer, comp, 4)
                    ?: continue

                glTexSubImage3D(
                    GL_TEXTURE_2D_ARRAY, 0, 0, 0, index,
                    width, height, 1, GL_RGBA, GL_UNSIGNED_BYTE, data
                )
                glGenerateMipmap(GL_TEXTURE_2D_ARRAY)
                STBImage.stbi_image_free(data)
            }

            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST)
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT)
        }
    }
}