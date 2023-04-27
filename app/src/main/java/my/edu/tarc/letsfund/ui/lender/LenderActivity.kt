package my.edu.tarc.letsfund.ui.lender

import android.os.Bundle
import android.widget.Toast
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.letsfund.ui.authentication.Users

class LenderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLenderBinding

    // To Store Payment Details
    data class PaymentHistory(
        var chosenMethod : String? = null,
        var transactionAmount : Double? = null
    )

    data class Wallet(
        var walletAmount : Double? = null,
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