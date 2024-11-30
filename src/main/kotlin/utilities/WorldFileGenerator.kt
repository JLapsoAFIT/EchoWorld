package utilities

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import utilities.data.WorldData

object WorldFileGenerator {
    @JvmStatic
    fun main(args: Array<String>) {
        val worldCreator = WorldCreator()
        val worldData = worldCreator.generateWorld()
        val json = Json.encodeToString(worldData)
        println(json)
    }
}