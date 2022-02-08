package nl.gewoonjaap.nfthub.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDataResponse (
    val nfts: List<NFTDataResponse>,
    val chain: String,
    val address: String
)