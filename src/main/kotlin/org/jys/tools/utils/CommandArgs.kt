package org.jys.tools.utils

import com.beust.jcommander.Parameter

class CommandArgs {

    @Parameter(names = ["-t", "--tool"], required = true, description = "toolName")
    lateinit var toolName: String

    @Parameter(names = ["-f1", "--file1"], description = "the first file or folder param")
    var file1: String? = null

    @Parameter(names = ["-f2", "--file2"], description = "the second file or folder param")
    var file2: String? = null

    @Parameter(names = ["--scale"], description = "the scale when compress picture")
    var scale: Double = 1.0

    @Parameter(names = ["--quality"], description = "the quality when compress picture")
    var quality: Float = 1.0f

}