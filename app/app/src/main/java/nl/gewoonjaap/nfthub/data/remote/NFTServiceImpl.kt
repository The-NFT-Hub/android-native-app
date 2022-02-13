package nl.gewoonjaap.nfthub.data.remote

import io.ktor.client.*
import io.ktor.client.request.*
import nl.gewoonjaap.nfthub.data.remote.dto.NFTExploreDataResponse

class NFTServiceImpl (private val client: HttpClient) : NFTService {
    override suspend fun getNFTExplore(): NFTExploreDataResponse? {
        return try {
            client.get {
                url("${HttpRoutes.NFT}/explore")
            }
        } catch(e: Exception){
            null
        }
    }
}