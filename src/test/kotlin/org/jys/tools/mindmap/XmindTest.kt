package org.jys.tools.mindmap


import com.fasterxml.jackson.core.type.TypeReference
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.jys.tools.mindmap.xmind.XmindContent
import org.jys.tools.utils.JsonUtil
import org.springframework.core.io.ClassPathResource

class XmindTest {

    @Test
    fun deserializeTest() {
        val file = ClassPathResource("/xmind/content.json")
        file.inputStream.use {
            val jsonData = IOUtils.toString(it)
            val contentObj = JsonUtil.mapper.readValue(jsonData,object : TypeReference<List<XmindContent>>() {})
            assertNotNull(contentObj)
        }
    }
}