package nl.gewoonjaap.nfthub

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import nl.gewoonjaap.nfthub.data.remote.UserProfileService
import nl.gewoonjaap.nfthub.data.remote.dto.UserProfileDataResponse
import nl.gewoonjaap.nfthub.helpers.StringHelper
import nl.gewoonjaap.nfthub.view.adapter.NFTCardAdapter


class ProfileActivity : AppCompatActivity() {

    private val client: UserProfileService = UserProfileService.create()
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private var address: String = ""
    private var chain: String = ""
    private var profileImage: ImageView? = null
    private var addressTextView: TextView? = null;
    private var recyclerView: RecyclerView? = null;
    private var adapter: NFTCardAdapter = NFTCardAdapter(emptyList())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        address = intent.getStringExtra(WALLET_ADDRESS).toString()
        chain = intent.getStringExtra(WALLET_CHAIN).toString()

        addressTextView = findViewById(R.id.WalletAddress)
        profileImage = findViewById(R.id.NFT_Profile_Image)

        setupRecyclerView()
        setupWalletAddressText()
        getNFTProfileData()

    }

    private fun setupRecyclerView(){
        recyclerView = findViewById(R.id.nft_card_recyclerView)
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        recyclerView!!.adapter = adapter


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

    override fun onStop() {
        super.onStop()
        cleanUp()
    }

    private fun getNFTProfileData() {
     scope.launch {
         val userProfile: UserProfileDataResponse? = client.getUserProfile(chain, address)
         if(userProfile != null){
             Toast.makeText(this@ProfileActivity, "Got Data, nfts: ${userProfile.nfts.size}", Toast.LENGTH_LONG).show()
             if(profileImage != null && userProfile.nfts.isNotEmpty()) {
                 val profileImageURL: String = userProfile.nfts.filter { it.metadata?.image != null }.random().metadata!!.image!!
                 Glide.with(this@ProfileActivity).load(profileImageURL)
                     .into(profileImage!!)
             }
             if(userProfile.nfts.isEmpty()){
                 Toast.makeText(this@ProfileActivity, "This wallet has no NFTs", Toast.LENGTH_LONG).show()
             }
             adapter = NFTCardAdapter(userProfile.nfts)
             recyclerView!!.adapter = adapter

         }
         else{
             Toast.makeText(this@ProfileActivity, "Invalid Wallet", Toast.LENGTH_LONG).show()
         }
     }
    }
    private fun cleanUp(){
        scope.cancel()
    }
}