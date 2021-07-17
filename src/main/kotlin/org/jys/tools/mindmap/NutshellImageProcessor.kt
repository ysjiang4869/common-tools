package org.jys.tools.mindmap

import net.lingala.zip4j.ZipFile
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.jys.tools.ToolsInterface
import org.jys.tools.mindmap.nutshell.NutshellContent
import org.jys.tools.mindmap.nutshell.NutshellNode
import org.jys.tools.utils.CommandArgs
import org.jys.tools.utils.JsonUtil
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

object NutshellImageProcessor : ToolsInterface {


    private fun convertNutshellImage(file: File, hostPrefix:String) {
        if (!FilenameUtils.isExtension(file.name, "nbmx")) {
            throw IllegalArgumentException("file is not nutshell mind map format")
        }

        val zipFile = ZipFile(file)
        val fileHeader = zipFile.getFileHeader("content.json") ?: return

        var nutshellContent: NutshellContent
        zipFile.getInputStream(fileHeader).use {
            val contentJson = IOUtils.toString(it,StandardCharsets.UTF_8)
            nutshellContent = JsonUtil.mapper.readValue(contentJson, NutshellContent::class.java)
        }
        val tempFolder = Paths.get(UUID.randomUUID().toString())
        val changedFile = ZipFile(tempFolder.resolve("temp.nbmx").toFile())
        val contentFile = tempFolder.resolve("content.json")

        nutshellContent.children?.forEach { replaceImage(it, hostPrefix) }
        FileUtils.writeStringToFile(contentFile.toFile(),
            JsonUtil.mapper.writeValueAsString(nutshellContent),StandardCharsets.UTF_8)
        changedFile.addFile(contentFile.toFile())

        val finalFile = Paths.get(file.parentFile.absolutePath, file.nameWithoutExtension + "_c.nbmx")
        Files.deleteIfExists(finalFile)
        Files.copy(
            tempFolder.resolve("temp.nbmx"),
            finalFile
        )
        FileUtils.forceDelete(tempFolder.toFile())
        println("process file[" + file.name + "] success")
    }

    private fun replaceImage(children: NutshellNode, hotsPrefix: String) {
        children.data.image?.let {
            children.data.image = "$hotsPrefix/$it"
        }
        children.children?.forEach { replaceImage(it, hotsPrefix) }
    }

    override fun handle(commandArgs: CommandArgs) {
        commandArgs.file1 ?: error("no file specified, use -f1 to specific file path")
        commandArgs.host ?: error("no host specified, use --host to specific host for image")
        convertNutshellImage(Paths.get(commandArgs.file1!!).toFile(),commandArgs.host!!)
    }

    override fun getCommandName(): String {
        return "nbmx-img-2-url"
    }
}