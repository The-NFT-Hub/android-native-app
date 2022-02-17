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
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.*
import nl.gewoonjaap.nfthub.data.remote.CollectionService
import nl.gewoonjaap.nfthub.data.remote.NFTService
import nl.gewoonjaap.nfthub.data.remote.dto.HotCollectionDataResponse
import nl.gewoonjaap.nfthub.data.remote.dto.NFTExploreDataResponse
import nl.gewoonjaap.nfthub.helpers.ChainSelectorHelper
import nl.gewoonjaap.nfthub.view.adapter.NFTCardSmallAdapter
import nl.gewoonjaap.nfthub.view.adapter.SimpleImageArrayAdapter


const val WALLET_ADDRESS = "nl.gewoonjaap.nfthub.WALLET_ADDRESS"
const val WALLET_CHAIN = "nl.gewoonjaap.nfthub.CHAIN"


class MainActivity : AppCompatActivity() {

    private val nftService: NFTService = NFTService.create()
    private val collectionService: CollectionService = CollectionService.create()
    private var scope = CoroutineScope(Job() + Dispatchers.Main)
    private var jobs: MutableList<Job> = mutableListOf()

    private var exploreNFTRecyclerView: RecyclerView? = null;
    private var hotCollectionRecyclerView: RecyclerView? = null;
    private var exploreNFTAdapter: NFTCardSmallAdapter = NFTCardSmallAdapter(emptyList())
    private var hotCollectionAdapter: NFTCardSmallAdapter = NFTCardSmallAdapter(emptyList())
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout = findViewById(R.id.main_swiperefreshlayout)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)


        setupRefreshLayout()
        setupWalletSearch()
        setupExploreRecyclerView()
        setupHotCollectionRecyclerView()
        getExploreNFTData()
        getHotCollectionData()


    }

    private fun setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
                getExploreNFTData()
                getHotCollectionData()
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

    private fun cleanUp(){
        scope.cancel()
        jobs.forEach {
            try {
                it.cancel("View closed")
            } catch(e: Exception){}
        }
        jobs = mutableListOf()
    }

    private fun getHotCollectionData() {
        swipeRefreshLayout.isRefreshing = true
        var job = scope.launch {
            val hotCollections: HotCollectionDataResponse? = collectionService.getHotCollection()
            swipeRefreshLayout.isRefreshing = false;
            if(hotCollections != null){
                Toast.makeText(this@MainActivity, "Got Data, hot collections: ${hotCollections.nfts.size}", Toast.LENGTH_LONG).show()

                if(hotCollections.nfts.isEmpty()){
                    Toast.makeText(this@MainActivity, "Hot collections is empty", Toast.LENGTH_LONG).show()
                }
                hotCollectionAdapter = NFTCardSmallAdapter(hotCollections.nfts)
                hotCollectionRecyclerView!!.adapter = hotCollectionAdapter

            }
            else{
                val params = Bundle()
                params.putString("error_hot_collections", "No Data returned")
                mFirebaseAnalytics.logEvent("eventErrorHotCollections", params)
                Toast.makeText(this@MainActivity, "Error while fetching hot collections", Toast.LENGTH_LONG).show()
            }
        }
        jobs.add(job)
    }

    private fun getExploreNFTData() {
        swipeRefreshLayout.isRefreshing = true
       var job = scope.launch {
            val exploreNfts: NFTExploreDataResponse? = nftService.getNFTExplore()
            swipeRefreshLayout.isRefreshing = false;
            if(exploreNfts != null){
                Toast.makeText(this@MainActivity, "Got Data, nfts: ${exploreNfts.nfts.size}", Toast.LENGTH_LONG).show()

                if(exploreNfts.nfts.isEmpty()){
                    Toast.makeText(this@MainActivity, "Explore NFTs is empty", Toast.LENGTH_LONG).show()
                }
                exploreNFTAdapter = NFTCardSmallAdapter(exploreNfts.nfts)
                exploreNFTRecyclerView!!.adapter = exploreNFTAdapter

            }
            else{
                val params = Bundle()
                params.putString("error_explore_nfts", "No Data returned")
                mFirebaseAnalytics.logEvent("eventErrorExploreNfts", params)

                Toast.makeText(this@MainActivity, "Error while fetching explore nfts", Toast.LENGTH_LONG).show()
            }
        }
        jobs.add(job)
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
        exploreNFTRecyclerView = findViewById(R.id.explore_nft_recycler_view)
        exploreNFTRecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exploreNFTRecyclerView!!.adapter = exploreNFTAdapter
    }

    private fun setupHotCollectionRecyclerView() {
        hotCollectionRecyclerView = findViewById(R.id.hotcollection_recycler_view)
        hotCollectionRecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        hotCollectionRecyclerView!!.adapter = hotCollectionAdapter
    }

}