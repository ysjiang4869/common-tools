package org.jys.tools

import org.jys.tools.utils.CommandArgs

interface ToolsInterface {

    fun handle(commandArgs: CommandArgs);

    fun getCommandName():String
}