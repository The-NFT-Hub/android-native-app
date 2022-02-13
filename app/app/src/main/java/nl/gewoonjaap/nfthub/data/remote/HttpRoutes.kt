package nl.gewoonjaap.nfthub.data.remote

import nl.gewoonjaap.nfthub.BuildConfig

object HttpRoutes {
    private const val ForceAccAPI: Boolean = true
    private const val BASE_URL_ACC = "https://api-acc-nfthub.mrproper.dev"
    private const val BASE_URL_DEV = "http://api-dev-nfthub.mrproper.dev:3000"
    var NFTProfile = "${getBaseUrl()}/account"
    var NFT = "${getBaseUrl()}/nft"
    var Collection = "${getBaseUrl()}/collection"

    private fun getBaseUrl() : String{
        if(BuildConfig.DEBUG && !ForceAccAPI){
            return BASE_URL_DEV
        }
        return BASE_URL_ACC
    }
}