package nl.gewoonjaap.nfthub.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NFTMetaDataResponse (
    val name: String? = "Unknown name",
    val description: String? = "Missing Description",
    val external_link: String? = null,
    val image: String? = null,
    val attributes: List<NFTAttributesResponse>? = emptyList()
    )