package org.jys.tools.mindmap

import com.fasterxml.jackson.core.type.TypeReference
import net.lingala.zip4j.ZipFile
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.jys.tools.ToolsInterface
import org.jys.tools.mindmap.nutshell.NutshellContent
import org.jys.tools.mindmap.nutshell.NutshellNode
import org.jys.tools.mindmap.xmind.*
import org.jys.tools.utils.CommandArgs
import org.jys.tools.utils.JsonUtil
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap

object NutshellToXmind : ToolsInterface {

    fun convertContent(content: NutshellContent): List<XmindContent> {
        val rootTopic = XmindRootTopic()
        rootTopic.id = UUID.randomUUID().toString()
        rootTopic.className = "topic"
        rootTopic.structureClass = "org.xmind.ui.map.unbalanced"
        rootTopic.title = content.data.text

        val children = XmindChildren()
        rootTopic.children = children
        nodeToChild(children, content.children)

        val xmindContent = XmindContent()
        xmindContent.id = UUID.randomUUID().toString()
        xmindContent.className = "sheet"
        xmindContent.title = "sheet 1"
        xmindContent.rootTopic = rootTopic
        xmindContent.theme =
            JsonUtil.mapper.readValue(XmindConst.themeString, object : TypeReference<Map<String, Any>>() {})
        return listOf(xmindContent)
    }

    fun convertNutshellFile(file: File) {
        if (!FilenameUtils.isExtension(file.name, "nbmx")) {
            throw IllegalArgumentException("file is not nutshell mind map format")
        }

        val zipFile = ZipFile(file)
        val fileHeader = zipFile.getFileHeader("content.json") ?: return

        val xmindContents: List<XmindContent>
        zipFile.getInputStream(fileHeader).use {
            val contentJson = IOUtils.toString(it,StandardCharsets.UTF_8)
            val nutshellContent = JsonUtil.mapper.readValue(contentJson, NutshellContent::class.java)
            xmindContents = convertContent(nutshellContent)
        }
        val tempFolder = Paths.get(UUID.randomUUID().toString())
        val xmindFile = ZipFile(tempFolder.resolve("temp.xmind").toFile())
        val contentFile = tempFolder.resolve("content.json")
        FileUtils.writeStringToFile(contentFile.toFile(),
            JsonUtil.mapper.writeValueAsString(xmindContents),StandardCharsets.UTF_8)
        xmindFile.addFile(contentFile.toFile())

        val metadataFile = tempFolder.resolve("metadata.json")
        FileUtils.writeStringToFile(metadataFile.toFile(),
            buildMetadata(xmindContents[0]),StandardCharsets.UTF_8)
        xmindFile.addFile(metadataFile.toFile())

        val nutShellResource = Paths.get(file.parentFile.absolutePath, file.nameWithoutExtension + "_nbmx_files")
        var xmindResource: Path? = null
        if (Files.exists(nutShellResource)) {
            xmindResource = tempFolder.resolve("resources")
            Files.createDirectory(xmindResource!!)
            Files.list(nutShellResource)
                .forEach { Files.copy(it, Paths.get(xmindResource.toString(), it.fileName.toString())) }
            xmindFile.addFolder(xmindResource.toFile())
        }

        val manifestFile = tempFolder.resolve("manifest.json")
        FileUtils.writeStringToFile(manifestFile.toFile(), buildManifest(xmindResource),StandardCharsets.UTF_8)
        xmindFile.addFile(manifestFile.toFile())

        val finalFile = Paths.get(file.parentFile.absolutePath, file.nameWithoutExtension + ".xmind")
        Files.deleteIfExists(finalFile)
        Files.copy(
            tempFolder.resolve("temp.xmind"),
            finalFile
        )
        FileUtils.forceDelete(tempFolder.toFile())
        println("convert file[" + file.name + "] success")
    }

    private fun nodeToChild(children: XmindChildren, nodes: List<NutshellNode>?) {
        val attached = nodes?.map { node ->
            XmindAttached().apply {
                id = UUID.randomUUID().toString()
                title = node.data.text
                if (node.children != null) {
                    val subChildren = XmindChildren()
                    this.children = subChildren
                    nodeToChild(subChildren, node.children)
                }
                href = node.data.hyperlink
                node.data.note?.let {
                    this.notes = XmindNote().apply { plain = XmindNote.Content().apply { content = it } }
                }
                node.data.image?.let {
                    image = XmindImage().apply { src = "xap:resources/" + node.data.getImageFileName() }
                }
            }
        }
        children.attached = attached
    }

    private fun buildMetadata(xmindContent: XmindContent): String {
        val creator = JsonUtil.newNode()
        creator.put("name", "jiangyuesong")
        creator.put("version", "10.2.1.202007271856")

        val meta = JsonUtil.newNode()
        meta.replace("creator", creator)
        meta.put("activeSheetId", xmindContent.id)
        return meta.toString()
    }

    private fun buildManifest(resourcesFolder: Path?): String {
        val fileEntries = HashMap<String, Any>()
        fileEntries["content.json"] = EmptyObject()
        fileEntries["metadata.json"] = EmptyObject()
        resourcesFolder?.let { folder ->
            Files.list(folder).forEach {
                fileEntries["resources/" + it.fileName] = EmptyObject()
            }
        }
        return JsonUtil.mapper.writeValueAsString(hashMapOf(Pair("file-entries", fileEntries)))
    }

    class EmptyObject {

    }

    override fun handle(commandArgs: CommandArgs) {
        commandArgs.file1 ?: error("no file specified, use -f1 to specific file path")
        convertNutshellFile(Paths.get(commandArgs.file1!!).toFile())
    }

    override fun getCommandName(): String {
        return "nbmx2xmind"
    }
}