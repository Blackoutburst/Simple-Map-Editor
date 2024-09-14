package dev.blackoutburst.sme.files

import java.io.File

object FileSystem {

    init {
        File("sme-assets").mkdirs()
        File("sme-assets/blocks").mkdirs()
        File("sme-assets/models").mkdirs()
    }
}