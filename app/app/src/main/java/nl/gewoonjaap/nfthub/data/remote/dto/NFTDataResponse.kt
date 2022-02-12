package nl.gewoonjaap.nfthub.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NFTDataResponse (
    val token_address: String,
    val token_id: String,
    val block_number_minted: String? = "Unknown mint blocknumber",
    val owner_of: String? = "Unknown",
    val block_number: String? = "Unknown blocknumber",
    val amount: String,
    val contract_type: String,
    val name: String? = "Unkown name",
    val symbol: String? = "Unknown Symbol",
    val token_uri: String? = null,
    val metadata: NFTMetaDataResponse? = null,
    val synced_at: String? = null,
    val is_valid: Int? = 0,
    val syncing: Int? = 0,
    val frozen: Int? = 0,
    val chain: String? = "eth"
    )