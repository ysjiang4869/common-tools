package org.jys.tools.mindmap.xmind

import com.fasterxml.jackson.annotation.JsonAlias
import org.jys.tools.mindmap.xmind.XmindChildren

class XmindRootTopic {

    lateinit var id: String

    @JsonAlias("class")
    lateinit var className: String

    lateinit var title: String

    lateinit var structureClass: String

    var titleUnedited = false

    lateinit var children: XmindChildren


}