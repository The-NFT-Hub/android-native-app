package nl.gewoonjaap.nfthub.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
class HotCollectionDataResponse (
    val nfts: List<NFTDataResponse>
)