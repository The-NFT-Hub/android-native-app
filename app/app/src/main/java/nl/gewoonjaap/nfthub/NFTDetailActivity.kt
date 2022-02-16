package nl.gewoonjaap.nfthub

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics

class NFTDetailActivity : AppCompatActivity() {
    lateinit var NFTImageView: ImageView
    lateinit var NFTNameText: TextView
    lateinit var NFTDescription: TextView
    lateinit var NFTCollectionText: TextView
    lateinit var chain: String
    lateinit var token_address: String

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private var NFT_NAME: String = "NFT_NAME"
    private var NFT_DESCRIPTION: String = "NFT_DESCRIPTION"
    private var NFT_COLLECTION: String = "NFT_COLLECTION"
    private var NFT_CHAIN: String = "NFT_CHAIN"
    private var NFT_ADDRESS: String = "NFT_ADDRESS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nftdetail)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        NFTImageView = findViewById(R.id.NFT_detail_image)
        NFTNameText = findViewById(R.id.NFT_detail_header)
        NFTDescription = findViewById(R.id.NFT_detail_description)
        NFTCollectionText = findViewById(R.id.NFT_detail_collection)

        NFTNameText.text = intent.getStringExtra(NFT_NAME)
        NFTDescription.text = intent.getStringExtra(NFT_DESCRIPTION)
        NFTCollectionText.text = intent.getStringExtra(NFT_COLLECTION)
        chain = intent.getStringExtra(NFT_CHAIN).toString()
        token_address = intent.getStringExtra(NFT_ADDRESS).toString()

        setupNFTImageView()
        setupOnCollectionClick()

        logScreenOpen()
    }

    private fun logScreenOpen(){
        val params = Bundle()
        params.putString("nft_name", intent.getStringExtra(NFT_NAME))
        params.putString("nft_collection", intent.getStringExtra(NFT_COLLECTION))
        params.putString("nft_chain", intent.getStringExtra(NFT_CHAIN))
        params.putString("nft_address", intent.getStringExtra(NFT_ADDRESS))
        mFirebaseAnalytics.logEvent("nft_detail_screen", params)
    }

    private fun setupOnCollectionClick() {

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
            .into(NFTImageView!!)
    }
}