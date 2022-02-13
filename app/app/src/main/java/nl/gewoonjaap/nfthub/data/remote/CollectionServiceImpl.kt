package nl.gewoonjaap.nfthub.data.remote

import io.ktor.client.*
import io.ktor.client.request.*
import nl.gewoonjaap.nfthub.data.remote.dto.HotCollectionDataResponse

class CollectionServiceImpl (private val client: HttpClient) : CollectionService{
    override suspend fun getHotCollection(): HotCollectionDataResponse? {
        return try {
            client.get {
                url("${HttpRoutes.Collection}/hot")
            }
        } catch(e: Exception){
            null
        }
    }
}