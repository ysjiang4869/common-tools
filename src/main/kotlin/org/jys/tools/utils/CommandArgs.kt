package org.jys.tools.utils

import com.beust.jcommander.Parameter

class CommandArgs {

    @Parameter(names= ["-t", "--tool"], required = true,description = "toolName")
    lateinit var toolName: String

    @Parameter(names = ["-f1", "--file1"], description = "the first file or folder param")
    var file1: String? = null

}