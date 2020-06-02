import extensions.writeXlmWithTags
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

internal open class ColorsTask : DefaultTask(){
    @get:OutputFile
    lateinit var outputFile: File

    @get:Input
    val colorsMap = mapOf(
        "color1" to "#00ff00",
        "color2" to "#ff0000",
        "color3" to "#00ffff",
        "color4" to "#00ffff"
    )

    @TaskAction
    fun makeResources() {
        colorsMap.entries.joinToString { (colorName, color) ->
            "\n    <color name=\"$colorName\">$color</color>"
        }.also { xml ->
            outputFile.writeXlmWithTags(xml)
        }
    }
}