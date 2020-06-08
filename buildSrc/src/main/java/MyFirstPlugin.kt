import extensions.android
import extensions.getAllPossibleFileLocationsByVariantDirectory
import extensions.variants
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

internal class MyFirstPlugin : Plugin<Project> {

    companion object {
        private const val COLOR_FILE_NAME = "my_colors.txt"
    }

    override fun apply(project: Project) {
        project.android().variants().all { variant ->

            // Make a task for each combination of build type and product flavor
            val colorTaskName = "generateColors${variant.name.capitalize()}"

            val outputPath = "${project.buildDir}/generated/res"

            // Register a simple task as a lambda. We'll later move it its own class and add some niceties
            project.tasks.register(colorTaskName, ColorsTask::class.java) { colorTask ->
                colorTask.group = "MyPluginTasks"

                val colorsFileLocations =
                    getAllPossibleFileLocationsByVariantDirectory(variant.dirName)

                val colorsFile = colorsFileLocations.asSequence()
                    .map { project.file("$it/$COLOR_FILE_NAME") }
                    .firstOrNull { it.isFile }
                    ?: throw GradleException(
                        "No $COLOR_FILE_NAME file found in any of the following locations: " +
                                "\n${colorsFileLocations.joinToString("\n")}"
                    )

                // We write our output in the build folder. Also note that we want to have a
                // reference to this so we can later mark it as a generated resource folder
                val outputDirectory =
                    File("$outputPath/${variant.dirName}").apply { mkdir() }
                colorTask.outputFile = File(outputDirectory, "values/generated_colors.xml")
                colorTask.inputFile = colorsFile

                // Marks the output directory as an app resource folder
                variant.registerGeneratedResFolders(
                    project.files(outputDirectory).builtBy(
                        colorTask
                    )
                )
            }
        }
    }
}