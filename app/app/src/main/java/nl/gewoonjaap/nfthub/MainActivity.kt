package nl.gewoonjaap.nfthub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import nl.gewoonjaap.nfthub.data.remote.NFTService
import nl.gewoonjaap.nfthub.data.remote.dto.NFTExploreDataResponse
import nl.gewoonjaap.nfthub.helpers.ChainSelectorHelper
import nl.gewoonjaap.nfthub.view.adapter.NFTCardSmallAdapter
import nl.gewoonjaap.nfthub.view.adapter.SimpleImageArrayAdapter

const val WALLET_ADDRESS = "nl.gewoonjaap.nfthub.WALLET_ADDRESS"
const val WALLET_CHAIN = "nl.gewoonjaap.nfthub.CHAIN"


class MainActivity : AppCompatActivity() {

    private val client: NFTService = NFTService.create()
    private var scope = CoroutineScope(Job() + Dispatchers.Main)

    private var recyclerView: RecyclerView? = null;
    private var adapter: NFTCardSmallAdapter = NFTCardSmallAdapter(emptyList())
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout = findViewById(R.id.main_swiperefreshlayout)


        setupRefreshLayout()
        setupWalletSearch()
        setupExploreRecyclerView()
        getExploreNFTData()


    }

    private fun setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
                getExploreNFTData()
        }
    }

    override fun onResume() {
        super.onResume()
        scope = CoroutineScope(Job() + Dispatchers.Main)
    }

    override fun onPause() {
        super.onPause()
        cleanUp()
    }

    private fun cleanUp() {
        scope.cancel()
    }

    private fun getExploreNFTData() {
        swipeRefreshLayout.isRefreshing = true
        scope.launch {
            val exploreNfts: NFTExploreDataResponse? = client.getNFTExplore()
            swipeRefreshLayout.isRefreshing = false;
            if(exploreNfts != null){
                Toast.makeText(this@MainActivity, "Got Data, nfts: ${exploreNfts.nfts.size}", Toast.LENGTH_LONG).show()

                if(exploreNfts.nfts.isEmpty()){
                    Toast.makeText(this@MainActivity, "Explore NFTs is empty", Toast.LENGTH_LONG).show()
                }
                adapter = NFTCardSmallAdapter(exploreNfts.nfts)
                recyclerView!!.adapter = adapter

            }
            else{
                Toast.makeText(this@MainActivity, "Error while fetching explore nfts", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupWalletSearch(){
        val getNFTButton: Button = findViewById(R.id.showNFTButton)
        val inputAddress: TextInputEditText = findViewById(R.id.WalletAddressInput)
        val spinner: Spinner = findViewById(R.id.chain_spinner)

        val spinnerAdapter = SimpleImageArrayAdapter(
            this,
            intArrayOf(
                R.drawable.ic_ethereum_eth_logo,
                R.drawable.ic_polygon_matic_logo,
                R.drawable.ic_binance_smart_chain_bnb_logo,
                R.drawable.ic_fantom_logo,
                R.drawable.ic_avalanche_avax_logo
            ).toTypedArray()
        )
        spinner.adapter = spinnerAdapter

        getNFTButton.setOnClickListener {
            val walletAddress = inputAddress.text.toString().trim()
            val chain: String = ChainSelectorHelper.getChainByDrawable(spinner.selectedItem.toString())
            Toast.makeText(this, "Wallet Address: $walletAddress chain: ${chain}", Toast.LENGTH_LONG).show()

            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra(WALLET_ADDRESS, walletAddress)
                putExtra(WALLET_CHAIN, chain)
            }
            startActivity(intent)
        }
    }

    private fun setupExploreRecyclerView(){
        recyclerView = findViewById(R.id.explore_nft_recycler_view)
        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.adapter = adapter
    }

}