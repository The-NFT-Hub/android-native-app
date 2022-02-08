package nl.gewoonjaap.nfthub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

const val WALLET_ADDRESS = "nl.gewoonjaap.nfthub.WALLET_ADDRESS"
const val WALLET_CHAIN = "nl.gewoonjaap.nfthub.CHAIN"


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val GetNFTButton: Button = findViewById(R.id.showNFTButton)
        val inputAddress: TextInputEditText = findViewById(R.id.WalletAddressInput)

        GetNFTButton.setOnClickListener {
            val walletAddress = inputAddress.text.toString().trim()
            Toast.makeText(this, "Wallet Address: $walletAddress", Toast.LENGTH_LONG).show()

            val intent = Intent(this, ProfileActivity::class.java).apply {
              putExtra(WALLET_ADDRESS, walletAddress)
                putExtra(WALLET_CHAIN, "eth")
          }
            startActivity(intent)
        }


    }
}