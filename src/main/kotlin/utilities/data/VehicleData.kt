package utilities.data

import utilities.enums.Locations
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * A serializable data class for vehicle objects utilized in [Vehicles]
 * @author Joshua A. Lapso
 *
 * @param position optional, default to random
 * @param draw_scan_lines optional, default to false
 * @param home optional, default to N/A
 */
data class VehicleData(
    val name: String,
    val draw_scan_lines: Boolean = false,
    val home: List<Int>? = null,
    override val position: List<Int> = listOf(
        Random.nextInt(Locations.DEFAULT.range[0], Locations.DEFAULT.range[1]),
        Random.nextInt(Locations.DEFAULT.range[0], Locations.DEFAULT.range[1])
    )
): AbstractData()
