package org.jys.tools.picture

import net.coobird.thumbnailator.Thumbnails
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * @author YueSong Jiang
 * @date 2020/09/12
 */
object Compressor {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun compress(file: Path, saveFolder: Path,
                 scale: Double, quality: Float) {
        if (!Files.exists(saveFolder)) {
            Files.createDirectories(saveFolder)
        }
        if (!Files.isDirectory(saveFolder)) throw IllegalArgumentException("save folder $saveFolder is not a directory")
        if (!isImage(file)) throw IllegalArgumentException("file $file is not image file")

        log.info("compress file ${file.toAbsolutePath()}")
        val fileName = file.toFile().nameWithoutExtension
        val extension = file.toFile().extension
        val saveFile = Paths.get(saveFolder.toAbsolutePath().toString(), "$fileName-compressed.$extension").toString()
        log.info("save to $saveFile")
        Thumbnails.of(file.toString())
                .scale(scale)
                .outputQuality(quality)
                .toFile(saveFile)
    }

    fun compressFolder(folder: Path, saveFolder: Path,
                       scale: Double, quality: Float) {
        Files.walk(folder).use { stream ->
            stream.filter { isImage(it) }
                    .forEach { compress(it, saveFolder, scale, quality) }
        }
    }

    private fun isImage(file: Path): Boolean {
        return !Files.isDirectory(file)
    }
}