import extensions.writeXlmWithTags
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

internal open class ColorsTask : DefaultTask(){
    @get:OutputFile
    lateinit var outputFile: File

    // We now take a file and will therefore change the annotation to reflect this
    @get:InputFile
    lateinit var inputFile: File

    @TaskAction
    fun makeResources() {
        // Simple CSV parsing mechanism. For more complex use cases we may want to use a dedicated library
        inputFile.readLines().map { color ->
            color.split(",")
        }.joinToString {
            "\n    <color name=\"${it[0]}\">${it[1]}</color>"
        }.also { xml ->
            outputFile.writeXlmWithTags(xml)
        }
    }
}