import android.databinding.tool.util.StringUtils
import java.util.*
import java.util.regex.Pattern

fun getAllPossibleFileLocationsByVariantDirectory(variantDirname: String): List<String> {

    val VARIANT_PATTERN =
        Pattern.compile("""(?:([^\p{javaUpperCase}]+)((?:\p{javaUpperCase}[^\p{javaUpperCase}]*)*)/)?([^/]*)""")
    val FLAVOR_PATTERN = Pattern.compile("""(\p{javaUpperCase}[^\p{javaUpperCase}]*)""")

    fun splitVariantNames(variant: String): List<String> {
        val flavors = arrayListOf<String>()
        val flavorMatcher = FLAVOR_PATTERN.matcher(variant)
        while (flavorMatcher.find()) {
            val match = flavorMatcher.group(1)
            if (match != null) {
                flavors.add(match.toLowerCase())
            }
        }
        return flavors
    }

    fun getFileLocations(variantDirname: String): List<String> {
        val variantMatcher = VARIANT_PATTERN.matcher(variantDirname)
        val fileLocations = arrayListOf<String>()
        if (!variantMatcher.matches()) {
            return fileLocations
        }
        val flavorNames = ArrayList<String>()
        if (variantMatcher.group(1) != null) {
            flavorNames.add(variantMatcher.group(1).toLowerCase())
        }
        if (variantMatcher.group(2) != null) {
            flavorNames.addAll(splitVariantNames(variantMatcher.group(2)).toList())
        }
        val buildType = variantMatcher.group(3)

        val group1 = variantMatcher.group(1)
        val group2 = variantMatcher.group(2)
        if (group1 != null && group2 != null) {
            val flavorName = group1 + group2
            fileLocations.add("src/$flavorName/$buildType")
            fileLocations.add("src/$buildType/$flavorName")
            fileLocations.add("src/$flavorName")
            fileLocations.add("src/" + flavorName + StringUtils.capitalize(buildType))
        }

        fileLocations.add("src/$buildType")

        var fileLocation = "src"
        for (flavor in flavorNames) {
            fileLocation += "/$flavor"
            fileLocations.add(fileLocation)
            fileLocations.add("$fileLocation/$buildType")
            fileLocations.add(fileLocation + StringUtils.capitalize(buildType))
        }

        fileLocations.add("src/main")

        return fileLocations.distinct().sortedBy {
            it.codePoints()?.filter { x: Int -> x == '/'.toInt() }?.count() ?: 0
        }.toList()
    }

    return getFileLocations(variantDirname)
}