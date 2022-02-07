package nl.gewoonjaap.nfthub

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val address = intent.getStringExtra(WALLET_ADDRESS)

        val addressTextView = findViewById<TextView?>(R.id.WalletAddress)
            addressTextView.apply {
            text = address
        }
        addressTextView.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", address)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Copied Wallet Address", Toast.LENGTH_LONG).show()
        }
    }
}