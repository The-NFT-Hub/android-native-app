package nl.gewoonjaap.nfthub

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.*
import nl.gewoonjaap.nfthub.data.remote.NFTService
import nl.gewoonjaap.nfthub.data.remote.dto.NFTDataResponse
import nl.gewoonjaap.nfthub.helpers.StringHelper

class NFTDetailActivity : AppCompatActivity() {
    private val client: NFTService = NFTService.create()
    private var scope = CoroutineScope(Job() + Dispatchers.Main)
    private var jobs: MutableList<Job> = mutableListOf()

    lateinit var NFTImageView: ImageView
    lateinit var NFTNameText: TextView
    lateinit var NFTDescription: TextView
    lateinit var NFTCollectionText: TextView
    lateinit var NFTOwnerText: TextView
    lateinit var chain: String
    lateinit var token_address: String
    lateinit var owner_address: String
    lateinit var token_ID: String
    lateinit var collection_name: String
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private var NFT_NAME: String = "NFT_NAME"
    private var NFT_DESCRIPTION: String = "NFT_DESCRIPTION"
    private var NFT_COLLECTION: String = "NFT_COLLECTION"
    private var NFT_CHAIN: String = "NFT_CHAIN"
    private var NFT_ADDRESS: String = "NFT_ADDRESS"
    private var NFT_OWNER: String = "NFT_OWNER"
    private var NFT_TOKEN_ID: String = "NFT_TOKEN_ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nftdetail)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        NFTImageView = findViewById(R.id.NFT_detail_image)
        NFTNameText = findViewById(R.id.NFT_detail_header)
        NFTDescription = findViewById(R.id.NFT_detail_description)
        NFTCollectionText = findViewById(R.id.NFT_detail_collection)
        NFTOwnerText = findViewById(R.id.NFT_detail_owner)
        swipeRefreshLayout = findViewById(R.id.main_swiperefreshlayout)

        NFTNameText.text = intent.getStringExtra(NFT_NAME)
        NFTDescription.text = intent.getStringExtra(NFT_DESCRIPTION)
        chain = intent.getStringExtra(NFT_CHAIN).toString()
        token_address = intent.getStringExtra(NFT_ADDRESS).toString()
        owner_address = intent.getStringExtra(NFT_OWNER).toString()
        token_ID = intent.getStringExtra(NFT_TOKEN_ID).toString()
        collection_name = intent.getStringExtra(NFT_COLLECTION).toString()

        setupNFTImageView()
        setupCollectionText()
        setupOwnerText()
        setupRefreshLayout()
        fetchNFTApiData()
        logScreenOpen()
    }

    override fun onPause() {
        super.onPause()
        cleanUp()
    }

    override fun onResume() {
        super.onResume()
        scope = CoroutineScope(Job() + Dispatchers.Main)
    }

    private fun setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            fetchNFTApiData()
        }
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

    private fun fetchNFTApiData() {
        swipeRefreshLayout.isRefreshing = true
        val job = scope.launch {
            val nftData: NFTDataResponse? = client.getNFTDetails(chain, token_address, token_ID)
            if(nftData != null){
                //Toast.makeText(this@NFTDetailActivity, "Got NFT Details Data", Toast.LENGTH_SHORT).show()
                if(nftData.metadata?.image ?: nftData.metadata?.animation_url != null) {
                    Glide.with(this@NFTDetailActivity)
                        .load(nftData.metadata?.image ?: nftData.metadata?.animation_url)
                        .placeholder(NFTImageView.drawable)
                        .into(NFTImageView)
                }
                if(nftData.owner_of != null){
                owner_address = nftData.owner_of
                    setupOwnerText()
                }
                if(nftData.metadata?.description != null && nftData.metadata.description.isNotEmpty()){
                    NFTDescription.text = nftData.metadata.description
                }
            }
            else{
                Toast.makeText(this@NFTDetailActivity, "Failed to get NFT Details data", Toast.LENGTH_SHORT).show()
            }
            swipeRefreshLayout.isRefreshing = false
        }
        jobs.add(job)
    }

    private fun setupOwnerText() {
        NFTOwnerText.text = StringHelper.ellipsize("Owner: $owner_address", 40)
        if(owner_address == "Unknown") return
        NFTOwnerText.setOnClickListener {

            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra(WALLET_ADDRESS, owner_address)
                putExtra(WALLET_CHAIN, chain)
            }
            ContextCompat.startActivity(this, intent, null)
        }
    }

    private fun logScreenOpen(){
        val params = Bundle()
        params.putString("nft_name", intent.getStringExtra(NFT_NAME))
        params.putString("nft_collection", intent.getStringExtra(NFT_COLLECTION))
        params.putString("nft_chain", intent.getStringExtra(NFT_CHAIN))
        params.putString("nft_address", intent.getStringExtra(NFT_ADDRESS))
        mFirebaseAnalytics.logEvent("nft_detail_screen", params)
    }

    private fun setupCollectionText() {
        NFTCollectionText.text =  StringHelper.ellipsize("Collection: $collection_name", 40)
        NFTCollectionText.setOnClickListener {

            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra(WALLET_ADDRESS, token_address)
                putExtra(WALLET_CHAIN, chain)
            }
            ContextCompat.startActivity(this, intent, null)
        }
    }

    private fun setupNFTImageView() {
        val imageURL = intent.getStringExtra("NFT_IMAGE")
        Glide.with(this).load(imageURL)
            .into(NFTImageView)
    }
}