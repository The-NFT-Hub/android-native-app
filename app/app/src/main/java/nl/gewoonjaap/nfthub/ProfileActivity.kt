package nl.gewoonjaap.nfthub

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.*
import nl.gewoonjaap.nfthub.data.remote.UserProfileService
import nl.gewoonjaap.nfthub.data.remote.dto.UserProfileDataResponse
import nl.gewoonjaap.nfthub.helpers.ChainSelectorHelper
import nl.gewoonjaap.nfthub.helpers.StringHelper
import nl.gewoonjaap.nfthub.view.adapter.NFTCardAdapter


class ProfileActivity : AppCompatActivity() {

    private val client: UserProfileService = UserProfileService.create()
    private var scope = CoroutineScope(Job() + Dispatchers.Main)
    private var jobs: MutableList<Job> = mutableListOf()

    private var address: String = ""
    private var chain: String = ""
    private var profileImage: ImageView? = null
    private var bannerImage: ImageView? = null
    private var chainImage: ImageView? = null
    private var addressTextView: TextView? = null;
    private var recyclerView: RecyclerView? = null;
    private var adapter: NFTCardAdapter = NFTCardAdapter(emptyList())
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        address = intent.getStringExtra(WALLET_ADDRESS).toString()
        chain = intent.getStringExtra(WALLET_CHAIN).toString()

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        addressTextView = findViewById(R.id.WalletAddress)
        profileImage = findViewById(R.id.NFT_Profile_Image)
        bannerImage = findViewById(R.id.profileBannerImage)
        chainImage = findViewById(R.id.NFT_Profile_Chain_Image)
        swipeRefreshLayout = findViewById(R.id.main_swiperefreshlayout)

        setupRefreshLayout()
        setupRecyclerView()
        setupWalletAddressText()
        setupChainImage()
        getNFTProfileData()

        logProfileActivityOpen()
    }

    private fun logProfileActivityOpen() {
        val params = Bundle()
        params.putString("nft_chain",  chain)
        params.putString("nft_address", address)
        mFirebaseAnalytics.logEvent("nft_profile_screen", params)
    }

    private fun setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            getNFTProfileData()
        }
    }

    private fun setupRecyclerView(){
        recyclerView = findViewById(R.id.nft_card_recyclerView)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = adapter
    }

    private fun setupChainImage(){
        chainImage!!.setImageResource(ChainSelectorHelper.getChainDrawableByName(chain))
    }

    private fun setupWalletAddressText(){
        if(addressTextView == null) return

        if(address.isEmpty()) address = "Unknown"
        addressTextView.apply {
            this!!.text = StringHelper.ellipsize( address, 15)
        }

        addressTextView!!.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", address)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Copied Wallet Address", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onPause() {
        super.onPause()
        cleanUp()
    }

    override fun onResume() {
        super.onResume()
        scope = CoroutineScope(Job() + Dispatchers.Main)
    }

    private fun getNFTProfileData() {
        swipeRefreshLayout.isRefreshing = true
     val job = scope.launch {
         val userProfile: UserProfileDataResponse? = client.getUserProfile(chain, address)
         if(userProfile != null){
             Toast.makeText(this@ProfileActivity, "Got Data, nfts: ${userProfile.nfts.size}", Toast.LENGTH_LONG).show()
             if(profileImage != null && userProfile.nfts.isNotEmpty()) {
                 val profileImageURL: String = userProfile.profileImage ?: userProfile.nfts.filter { it.metadata?.image != null }.random().metadata!!.image!!
                 Glide.with(this@ProfileActivity).load(profileImageURL)
                     .into(profileImage!!)
             }

             if(userProfile.profileBanner != null && userProfile.profileBanner.isNotEmpty()){
                 Glide.with(this@ProfileActivity).load(userProfile.profileBanner).into(bannerImage!!)
             }
             else{
                 bannerImage!!.setColorFilter(Color.parseColor("#${address.takeLast(6)}"))
             }

             if(userProfile.nfts.isEmpty()){
                 Toast.makeText(this@ProfileActivity, "This wallet has no NFTs", Toast.LENGTH_LONG).show()
             }
             adapter = NFTCardAdapter(userProfile.nfts)
             recyclerView!!.adapter = adapter

         }
         else{
             val params = Bundle()
             params.putString("error_nft_profile", "No Data returned")
             params.putString("error_nft_profile_chain", chain)
             params.putString("error_nft_profile_address", address)
             mFirebaseAnalytics.logEvent("eventErrorNftProfile", params)

             Toast.makeText(this@ProfileActivity, "Invalid Wallet", Toast.LENGTH_LONG).show()
         }
         swipeRefreshLayout.isRefreshing = false
     }
        jobs.add(job)
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
}