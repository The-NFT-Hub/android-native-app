package nl.gewoonjaap.nfthub

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val address = intent.getStringExtra(WALLET_ADDRESS)

        findViewById<TextView?>(R.id.WalletAddress).apply {
            text = address
        }
    }
}