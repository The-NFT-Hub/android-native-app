package nl.gewoonjaap.nfthub

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import nl.gewoonjaap.nfthub.data.remote.UserProfileService
import nl.gewoonjaap.nfthub.data.remote.dto.UserProfileDataResponse


class ProfileActivity : AppCompatActivity() {

    private val client: UserProfileService = UserProfileService.create()
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private var address: String = ""
    private var chain: String = ""
    private var profileImage: ImageView? = null
    private var addressTextView: TextView? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        address = intent.getStringExtra(WALLET_ADDRESS).toString()
        chain = intent.getStringExtra(WALLET_CHAIN).toString()

        addressTextView = findViewById(R.id.WalletAddress)
        profileImage = findViewById(R.id.NFT_Profile_Image)

        setupWalletAddressText()
        getNFTProfileData()

    }

    private fun setupWalletAddressText(){
        if(addressTextView == null) return


        if(address.isEmpty()) address = "Unknown"
        addressTextView.apply {
            this!!.text = address
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
             if(profileImage != null) {
                 Glide.with(this@ProfileActivity).load(userProfile.nfts.random().metadata?.image)
                     .into(profileImage!!)
             }
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