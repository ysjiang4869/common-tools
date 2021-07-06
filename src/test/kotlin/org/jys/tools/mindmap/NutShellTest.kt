package org.jys.tools.mindmap


import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.jys.tools.mindmap.nutshell.NutshellContent
import org.jys.tools.utils.JsonUtil
import org.springframework.core.io.ClassPathResource
import java.nio.file.Paths

class NutShellTest {


    @Test
    fun deserializeTest() {
        assertNotNull(getContent())
    }

    @Test
    fun convertContent() {
        val content = getContent()
        val xmindContent = content?.let { NutshellToXmind.convertContent(it) }
        assertNotNull(xmindContent)
        FileUtils.writeStringToFile(
                Paths.get("nutshell_2_xmind", "content.json").toFile(),
                JsonUtil.mapper.writeValueAsString(xmindContent)
        )
    }

    @Test
    fun convertFile() {
        NutshellToXmind.convertNutshellFile(Paths.get("nutshell_2_xmind/jianguoyun_data.nbmx").toFile())
    }

    private fun getContent(): NutshellContent? {
        val file = ClassPathResource("/nutshell/data.json")
        var contentObj: NutshellContent?
        file.inputStream.use {
            val jsonData = IOUtils.toString(it)
            contentObj = JsonUtil.mapper.readValue(jsonData, NutshellContent::class.java)
            assertNotNull(contentObj)
        }
        return contentObj
    }
}