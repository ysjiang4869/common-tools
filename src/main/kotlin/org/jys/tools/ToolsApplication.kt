package org.jys.tools

import com.beust.jcommander.JCommander
import org.jys.tools.mindmap.NutshellImageProcessor
import org.jys.tools.mindmap.NutshellToXmind
import org.jys.tools.picture.Compressor
import org.jys.tools.utils.CommandArgs
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ToolsApplication

private var tools= arrayOf(NutshellToXmind, Compressor,NutshellImageProcessor)

fun main(args: Array<String>) {
    runApplication<ToolsApplication>(*args)
    val command = CommandArgs()
    JCommander.newBuilder()
        .addObject(command)
        .build()
        .parse(*args)
    val handler = tools.firstOrNull { it.getCommandName() == command.toolName }
        ?: error("no tools with name:" + command.toolName)
    handler.handle(command)
}
