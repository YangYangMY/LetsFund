package my.edu.tarc.letsfund.ui.lender

import android.os.Bundle
import androidx.activity.compose.setContent
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.ActivityLenderBinding
import androidx.appcompat.widget.Toolbar;
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class LenderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLenderBinding

    // To Store Payment Details
    data class PaymentHistory(
        var transactionDate : String? = null,
        var chosenMethod : String? = null,
        var transactionAmount : Double? = null
    )

    data class Wallet(
        var walletAmount : Double? = null,
        var paymentHistory : PaymentHistory? = null
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_lender)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        navView.setupWithNavController(navController)
    }


}