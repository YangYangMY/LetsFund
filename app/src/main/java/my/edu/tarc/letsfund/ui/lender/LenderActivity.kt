package my.edu.tarc.letsfund.ui.lender

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.ActivityLenderBinding
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity

class LenderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLenderBinding

    //Initialize Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var storageReference: StorageReference
    private lateinit var uri: Uri

    private lateinit var uid: String

    //recycle view
    private lateinit var FundListRecyclerView: RecyclerView
    private lateinit var FundList : ArrayList<BorrowerActivity.BorrowRequest>

    // To Store Payment Details
    data class PaymentHistory(
        var date: String? = null,
        var method : String? = null,
        var amount : Double? = null
    )

    data class Wallet(
        var walletAmount : Double? = null,
    )

    data class LendHistory(
        var lendDate : String? = null,
        var borrowerName : String? = null,
        var loanAmount : Double? = null,
        var repaymentStatus: String? = null, //Unpaid Paid
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

        //Initialise Firebase
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("FundList").child(auth.currentUser!!.uid)

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }


}