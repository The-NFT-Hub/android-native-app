package nl.gewoonjaap.nfthub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import nl.gewoonjaap.nfthub.helpers.ChainSelectorHelper
import nl.gewoonjaap.nfthub.view.adapter.SimpleImageArrayAdapter

const val WALLET_ADDRESS = "nl.gewoonjaap.nfthub.WALLET_ADDRESS"
const val WALLET_CHAIN = "nl.gewoonjaap.nfthub.CHAIN"


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val GetNFTButton: Button = findViewById(R.id.showNFTButton)
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

        GetNFTButton.setOnClickListener {
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
}