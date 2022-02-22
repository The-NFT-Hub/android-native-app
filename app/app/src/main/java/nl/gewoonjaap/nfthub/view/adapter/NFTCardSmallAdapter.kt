package nl.gewoonjaap.nfthub.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nl.gewoonjaap.nfthub.*
import nl.gewoonjaap.nfthub.data.remote.dto.NFTDataResponse

class NFTCardSmallAdapter(private val nftList: List<NFTDataResponse>):
    RecyclerView.Adapter<NFTCardSmallAdapter.ViewHolder>(){

    private var parentView: ViewGroup? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.nft_card_small, parent,false)

        parentView = parent

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nftItem: NFTDataResponse = nftList[position]

        try {

            Glide.with(holder.imageView)
                .load(nftItem.metadata?.image)
                .into(holder.imageView)
        } catch (E: Exception){}

        holder.nameText.text = nftItem.metadata?.name ?: "${nftItem.name ?: "Unknown Name"} #${nftItem.token_id}"
        holder.collectionText.text =  nftItem.name

        holder.collectionText.setOnClickListener {
            val walletAddress = nftItem.token_address
            val chain: String = nftItem.chain!!
            //Toast.makeText(parentView!!.context, "Wallet Address: $walletAddress chain: $chain", Toast.LENGTH_LONG).show()

            val intent = Intent(parentView!!.context, ProfileActivity::class.java).apply {
                putExtra(WALLET_ADDRESS, walletAddress)
                putExtra(WALLET_CHAIN, chain)
            }
            ContextCompat.startActivity(parentView!!.context, intent, null)
        }

        holder.imageView.setOnClickListener {
            val intent = Intent(parentView!!.context, NFTDetailActivity::class.java).apply {
                putExtra("NFT_IMAGE", nftItem.metadata?.image)
                putExtra("NFT_NAME", nftItem.metadata?.name ?: "${nftItem.name ?: "Unknown Name"} #${nftItem.token_id}")
                putExtra("NFT_DESCRIPTION", nftItem.metadata?.description?: "Missing Description")
                putExtra("NFT_COLLECTION", nftItem.name)
                putExtra("NFT_CHAIN", nftItem.chain)
                putExtra("NFT_ADDRESS", nftItem.token_address)
                putExtra("NFT_OWNER", nftItem.owner_of)
                putExtra("NFT_TOKEN_ID", nftItem.token_id)
            }
            ContextCompat.startActivity(parentView!!.context, intent, null)
        }

    }

    override fun getItemCount(): Int {
        return nftList.size
    }

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {

        val imageView: ImageView = ItemView.findViewById(R.id.nft_image_card)
        val nameText: TextView = ItemView.findViewById(R.id.nft_name_card)
        val collectionText: TextView = ItemView.findViewById(R.id.nft_collection_card)

    }

}