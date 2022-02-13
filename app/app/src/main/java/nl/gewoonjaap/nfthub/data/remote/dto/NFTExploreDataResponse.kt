package nl.gewoonjaap.nfthub.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NFTExploreDataResponse (
    val nfts: List<NFTDataResponse>
    )