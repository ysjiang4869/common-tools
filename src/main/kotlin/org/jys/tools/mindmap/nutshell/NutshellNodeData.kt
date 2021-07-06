package org.jys.tools.mindmap.nutshell

import com.fasterxml.jackson.annotation.JsonAlias

class NutshellNodeData {

    lateinit var text: String

    @JsonAlias("connect-color")
    lateinit var connectColor: String

    var expandState: String? = null

    var layout: String? = null

    @JsonAlias("layout_right_offset")
    var layoutRightOffset: String? = null

    @JsonAlias("layout_left_offset")
    var layoutLeftOffset: String? = null

    @JsonAlias("layout_mind_offset")
    var layoutMindOffset: String? = null

    var hyperlink: String? = null

    var hyperlinkTitle: String? = null

    var note: String? = null

    var image: String? = null

    var imageTitle: String? = null

    fun getImageFileName():String{
        val filePath=image!!.split("/")[1]
        return filePath.split("?")[0]
    }

}