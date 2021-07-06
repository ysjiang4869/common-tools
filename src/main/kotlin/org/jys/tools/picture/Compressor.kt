package org.jys.tools.picture

import net.coobird.thumbnailator.Thumbnails
import org.jys.tools.ToolsInterface
import org.jys.tools.utils.CommandArgs
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory


/**
 * @author YueSong Jiang
 * @date 2020/09/12
 */
object Compressor : ToolsInterface {

    private val log = LoggerFactory.getLogger(this::class.java)

    private fun compress(
        file: Path, saveFolder: Path,
        scale: Double, quality: Float
    ) {
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

    fun compressFolder(
        folder: Path, saveFolder: Path,
        scale: Double, quality: Float
    ) {
        Files.walk(folder).use { stream ->
            stream.filter { isImage(it) }
                .forEach { compress(it, saveFolder, scale, quality) }
        }
    }

    private fun isImage(file: Path): Boolean {
        return !Files.isDirectory(file)
    }

    override fun handle(commandArgs: CommandArgs) {
        commandArgs.file1 ?: error("no file specified, use -f1 to specific file path or folder path")
        commandArgs.file2 ?: error("no output folder specified, use -f2 to specific folder path")
        val file=Paths.get(commandArgs.file1!!)
        if(file.isDirectory()){
            compressFolder(file,Paths.get(commandArgs.file2!!),commandArgs.scale,commandArgs.quality)
        }else{
            compressFolder(file,Paths.get(commandArgs.file2!!),commandArgs.scale,commandArgs.quality)
        }
    }

    override fun getCommandName(): String {
        return "compress-pic"
    }
}