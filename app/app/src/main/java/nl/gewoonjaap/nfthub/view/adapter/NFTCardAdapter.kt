package nl.gewoonjaap.nfthub.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nl.gewoonjaap.nfthub.ProfileActivity
import nl.gewoonjaap.nfthub.R
import nl.gewoonjaap.nfthub.WALLET_ADDRESS
import nl.gewoonjaap.nfthub.WALLET_CHAIN
import nl.gewoonjaap.nfthub.data.remote.dto.NFTDataResponse
import nl.gewoonjaap.nfthub.helpers.StringHelper

class NFTCardAdapter(private val nftList: List<NFTDataResponse>):
    RecyclerView.Adapter<NFTCardAdapter.ViewHolder>() {

    var parentView: ViewGroup? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.nft_card, parent,false)

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
        holder.descriptionText.text = StringHelper.ellipsize(nftItem.metadata?.description ?: "Missing Description", 1500)


        holder.collectionText.setOnClickListener {
            val walletAddress = nftItem.token_address
            val chain: String = nftItem.chain!!
            Toast.makeText(parentView!!.context, "Wallet Address: $walletAddress chain: ${chain}", Toast.LENGTH_LONG).show()

            val intent = Intent(parentView!!.context, ProfileActivity::class.java).apply {
                putExtra(WALLET_ADDRESS, walletAddress)
                putExtra(WALLET_CHAIN, chain)
            }
            startActivity(parentView!!.context, intent, null)
        }


    }

    override fun getItemCount(): Int {
        return nftList.size
    }

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {

        val imageView: ImageView = ItemView.findViewById(R.id.nft_image_card)
        val nameText: TextView = ItemView.findViewById(R.id.nft_name_card)
        val collectionText: TextView = ItemView.findViewById(R.id.nft_collection_card)
        val descriptionText: TextView = ItemView.findViewById(R.id.nft_description_card)

    }
}