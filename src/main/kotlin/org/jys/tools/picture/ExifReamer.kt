package org.jys.tools.picture

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.file.FileSystemDirectory
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory
import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory
import org.springframework.util.FileCopyUtils
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


object ExifReamer {

    private val log = LoggerFactory.getLogger(this::class.java)

    val failedFiles = ArrayList<String>(1024)
    val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    fun rename(filePath: Path,saveFolder:Path): Boolean {
        val metadata = ImageMetadataReader.readMetadata(filePath.toFile())
        try {
            var date = metadata?.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
                    ?.getDateDigitized(TimeZone.getTimeZone(ZoneOffset.ofHours(+8)))
            if (date == null) {
                date = metadata?.getFirstDirectoryOfType(QuickTimeMetadataDirectory::class.java)
                        ?.getDate(QuickTimeMetadataDirectory.TAG_CREATION_DATE)
            }
            if(date==null){
                date=metadata?.getFirstDirectoryOfType(FileSystemDirectory::class.java)
                        ?.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE, TimeZone.getTimeZone(ZoneOffset.ofHours(+8)))
            }
            date?.let {
                val timeStr = outputFormatter.format(LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.ofHours(+8)))
                val ext = FilenameUtils.getExtension(filePath.fileName.toString())
                val folder = filePath.parent
                val originName = FilenameUtils.getBaseName(filePath.fileName.toString())
                FileCopyUtils.copy(filePath.toFile(), Paths.get(saveFolder.toString(), "${originName}_$timeStr.$ext").toFile())
                return true
            }
        } catch (e: Exception) {
            log.error(e.message, e)
            return false
        }
        return false
    }

    fun renameFolder(folder: Path, outFolder: Path, extensions: Array<String>) {
        failedFiles.clear()
        Files.walk(folder).use { stream ->
            stream.filter {
                var valid = true
                if (Files.isDirectory(it)) {
                    valid = false
                }
                val ext = FilenameUtils.getExtension(it.fileName.toString())
                if (extensions.isNotEmpty()) {
                    valid = valid && extensions.contains(ext)
                }
                valid
            }.forEach {
                val result=rename(it,outFolder)
                if(!result) failedFiles.add(it.fileName.toString())
            }
        }
        println(failedFiles)
    }
}