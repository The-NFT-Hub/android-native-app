package nl.gewoonjaap.nfthub.helpers

import nl.gewoonjaap.nfthub.R

class ChainSelectorHelper {
    companion object {
        fun getChainByDrawable(drawable: String): String{
            return when(drawable.toInt()){
                R.drawable.ic_avalanche_avax_logo -> "avalanche"
                R.drawable.ic_binance_smart_chain_bnb_logo -> "bsc"
                R.drawable.ic_ethereum_eth_logo -> "eth"
                R.drawable.ic_fantom_logo -> "fantom"
                R.drawable.ic_polygon_matic_logo -> "polygon"
                else -> {
                    "eth"
                }
            }
        }
        fun getChainDrawableByName(chain: String): Int {
            return when(chain){
                "avalanche" -> R.drawable.ic_avalanche_avax_logo
                "bsc" -> R.drawable.ic_binance_smart_chain_bnb_logo
                "eth" -> R.drawable.ic_ethereum_eth_logo
                "fantom" -> R.drawable.ic_fantom_logo
                "polygon" ->  R.drawable.ic_polygon_matic_logo
                else -> {
                    R.drawable.ic_ethereum_eth_logo
                }
            }
        }
    }
}