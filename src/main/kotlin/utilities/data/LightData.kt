package utilities.data

import kotlinx.serialization.Serializable

/**
 * A serializable data class for light objects utilized in
 *
 *
 * @author Joshua A. Lapso
 * @param bound_key Optional, default to unbound
 * @param position Location (required)
 */
@Serializable
data class LightData(
    val bound_key: Int,
    override val position: List<Int>
) : AbstractData()