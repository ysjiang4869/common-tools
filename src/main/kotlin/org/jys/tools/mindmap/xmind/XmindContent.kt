package org.jys.tools.mindmap.xmind

import com.fasterxml.jackson.annotation.JsonAlias

class XmindContent {

    lateinit var id: String

    @JsonAlias("class")
    lateinit var className: String

    lateinit var title: String

    lateinit var rootTopic: XmindRootTopic

    var topicPositioning = "fixed"

    lateinit var theme:Map<String,Any>
}