package nl.gewoonjaap.nfthub.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NFTDataResponse (
    val token_address: String,
    val token_id: String,
    val block_number_minted: String,
    val owner_of: String,
    val block_number: String,
    val amount: String,
    val contract_type: String,
    val name: String,
    val symbol: String,
    val token_uri: String,
    val metadata: NFTMetaDataResponse,
    val synced_at: String? = null,
    val is_valid: Int,
    val syncing: Int,
    val frozen: Int
    )