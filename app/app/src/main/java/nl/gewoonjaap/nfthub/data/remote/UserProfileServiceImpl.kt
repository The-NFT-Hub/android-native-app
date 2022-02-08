package nl.gewoonjaap.nfthub.data.remote

import io.ktor.client.*
import io.ktor.client.request.*
import nl.gewoonjaap.nfthub.data.remote.dto.UserProfileDataResponse

class UserProfileServiceImpl (private val client: HttpClient) : UserProfileService {
    override suspend fun getUserProfile(chain: String, address: String): UserProfileDataResponse? {
        return try {
            client.get {
                url("${HttpRoutes.NFTProfile}/$chain/$address")
            }
        } catch(e: Exception){
            null
        }
    }
}