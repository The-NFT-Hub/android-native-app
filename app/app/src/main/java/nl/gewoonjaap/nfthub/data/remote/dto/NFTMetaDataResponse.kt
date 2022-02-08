package nl.gewoonjaap.nfthub.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NFTMetaDataResponse (
    val name: String,
    val external_link: String? = null,
    val image: String,
    val attributes: List<NFTAttributesResponse>? = emptyList()
    )