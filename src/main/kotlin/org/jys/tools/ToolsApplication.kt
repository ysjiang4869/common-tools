package org.jys.tools

import com.beust.jcommander.JCommander
import org.jys.tools.mindmap.NutshellToXmind
import org.jys.tools.utils.CommandArgs
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.file.Paths

@SpringBootApplication
class ToolsApplication

fun main(args: Array<String>) {
    runApplication<ToolsApplication>(*args)
    val command = CommandArgs()
    JCommander.newBuilder()
        .addObject(command)
        .build()
        .parse(*args)
    when (command.toolName) {
        "nbmx2xmind" -> nbmx2Xmind(command)
        else -> {
            print("unknown command")
        }
    }
}

fun nbmx2Xmind(commandArgs: CommandArgs) {
    if (commandArgs.file1 == null) {
        error("no file specified, use -f1 to specific file path")
    } else {
        NutshellToXmind.convertNutshellFile(Paths.get(commandArgs.file1!!).toFile())
    }
}
