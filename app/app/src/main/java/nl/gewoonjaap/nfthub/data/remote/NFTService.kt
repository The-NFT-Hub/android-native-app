package nl.gewoonjaap.nfthub.data.remote

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import nl.gewoonjaap.nfthub.data.remote.dto.NFTDataResponse
import nl.gewoonjaap.nfthub.data.remote.dto.NFTExploreDataResponse

interface NFTService {

    suspend fun getNFTExplore(): NFTExploreDataResponse?
    suspend fun getNFTDetails(chain: String, token_address: String, token_id: String): NFTDataResponse?

    companion object{
        fun create(): NFTService{
            return NFTServiceImpl(
                client = HttpClient(Android) {
                    install(Logging){
                        level = LogLevel.ALL
                    }
                    install(JsonFeature){
                        serializer = KotlinxSerializer(json)
                    }
                }
            )
        }
        private val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }
    }
}