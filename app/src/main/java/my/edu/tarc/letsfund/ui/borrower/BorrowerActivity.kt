package my.edu.tarc.letsfund.ui.borrower

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.ActivityBorrowerBinding

class BorrowerActivity : AppCompatActivity() {

    data class Borrow(
        var borrowerName : String? = null,
        var loanAmount : Double? = null,
        var location : String? = null,
        var description : String? = null,
        var fundStatus : String? = null,
        var repaymentDate : String? = null,
        var repaymentStatus : String? = null
    )

    private lateinit var binding: ActivityBorrowerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBorrowerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_borrower)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)
    }
}