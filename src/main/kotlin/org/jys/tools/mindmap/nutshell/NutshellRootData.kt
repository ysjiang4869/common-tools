package org.jys.tools.mindmap.nutshell

import com.fasterxml.jackson.annotation.JsonAlias

class NutshellRootData {

    lateinit var expandState: String

    lateinit var text: String

    @JsonAlias("font-family")
    lateinit var fontFamily: String

    @JsonAlias("font-weight")
    lateinit var fontWeight: String

}