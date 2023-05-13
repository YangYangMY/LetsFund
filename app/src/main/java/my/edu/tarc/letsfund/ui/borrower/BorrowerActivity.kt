package my.edu.tarc.letsfund.ui.borrower

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.textfield.TextInputEditText
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.ActivityBorrowerBinding

class BorrowerActivity : AppCompatActivity() {

    data class BorrowRequest(
        var loanAmount: String? = "",
        var borrowerName: String? = "",
        var loanTitle: String? = "",
        var loanDesc: String? = "",
        var loanReqEndDate: String? = "",
        var status: String? = "", //In Request, Lend
        var lenderID: String? = null, //Update after lend
    )

    data class RepaymentHistory(
        var date: String? = null,
        var lenderName: String? = null,
        var loanAmount: Double? = null,
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

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}