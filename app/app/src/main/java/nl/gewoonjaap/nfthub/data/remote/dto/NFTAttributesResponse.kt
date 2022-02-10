package nl.gewoonjaap.nfthub.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NFTAttributesResponse (
    val trait_type: String? = "Unknown",
    val value: String? = "Unknown"
    )