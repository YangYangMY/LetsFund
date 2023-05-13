package my.edu.tarc.letsfund.ui.borrower.repayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.FragmentRepaymentBinding
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity
import my.edu.tarc.letsfund.ui.lender.LenderActivity
import my.edu.tarc.letsfund.ui.lender.wallet.TransactionAdapter

class RepaymentFragment : Fragment() {
    private lateinit var composeView: ComposeView
    private var _binding: FragmentRepaymentBinding? = null

    //Color
    val light_green = Color(0xFF599D42)
    val light_green_500 = Color(0xFF8BC34A)
    val light_orange = Color(0xFFF6BD60)
    private val binding get() = _binding!!

    //Initialise Database
    private lateinit var uid: String
    private lateinit var databaseRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    //recycle view
    private lateinit var repaymentRecyclerView: RecyclerView
    private lateinit var loadingRepayment: ProgressBar
    private lateinit var repaymentList: ArrayList<BorrowerActivity.RepaymentHistory>

    private lateinit var loanStatus: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRepaymentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //initialise database
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance()

        val databaseRefLoan = database.reference.child("Loans").child(uid)

        databaseRefLoan.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    loanStatus =
                        snapshot.getValue(BorrowerActivity.BorrowRequest::class.java)?.status.toString()

                    if (loanStatus == "Borrowed") {
                        composeView = binding.repaymentbox
                        composeView.setContent {
                            val repayAmount = remember { mutableStateOf<Double?>(null) }
                            getRepayAmount { amount ->
                                repayAmount.value = amount
                            }
                            Card() {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(light_orange)
                                ) {
                                    Text(
                                        "Repayment Amount", Modifier.padding(all = 20.dp),
                                        color = Color.White, fontSize = 17.sp, fontFamily = FontFamily.SansSerif
                                    )
                                    Text(
                                        "RM ${repayAmount.value ?: 0.00}",
                                        Modifier
                                            .padding(top = 50.dp)
                                            .padding(horizontal = 19.dp),
                                        fontWeight = FontWeight.Bold, color = Color.White,
                                        fontSize = 22.sp, fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }
                        }
                        getRepayAmount { amount ->
                            if (amount != null) {
                                if (amount.equals(0.0)) {
                                    composeView = binding.btnRepay
                                    composeView.setContent {
                                        Button(
                                            enabled = false,
                                            onClick = {
                                                findNavController().navigate(R.id.action_navigation_repayment_to_borrowerPaymentFragment)
                                            },
                                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                                            colors = ButtonDefaults.outlinedButtonColors(light_green)
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_attach_money_24),
                                                contentDescription = "Localized description",
                                                modifier = Modifier.size(ButtonDefaults.IconSize),
                                                tint = Color.White
                                            )
                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            Text("Repay", color = Color.White)
                                        }
                                    }
                                } else {
                                    composeView = binding.btnRepay
                                    composeView.setContent {
                                        Button(
                                            enabled = true,
                                            onClick = {
                                                findNavController().navigate(R.id.action_navigation_repayment_to_borrowerPaymentFragment)
                                            },
                                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                                            colors = ButtonDefaults.outlinedButtonColors(light_green)
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_attach_money_24),
                                                contentDescription = "Localized description",
                                                modifier = Modifier.size(ButtonDefaults.IconSize),
                                                tint = Color.White
                                            )
                                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                            Text("Repay", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        //Status is Not borrowed
                        composeView = binding.repaymentbox
                        composeView.setContent {
                            Card() {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(light_orange)
                                ) {
                                    Text(
                                        "Repayment Amount", Modifier.padding(all = 20.dp),
                                        color = Color.White, fontSize = 17.sp, fontFamily = FontFamily.SansSerif
                                    )
                                    Text(
                                        "RM 0.0",
                                        Modifier
                                            .padding(top = 50.dp)
                                            .padding(horizontal = 19.dp),
                                        fontWeight = FontWeight.Bold, color = Color.White,
                                        fontSize = 22.sp, fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }
                        }
                        getRepayAmount { amount ->

                            composeView = binding.btnRepay
                            composeView.setContent {
                                Button(
                                    enabled = false,
                                    onClick = {
                                    },
                                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                                    colors = ButtonDefaults.outlinedButtonColors(light_green)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_attach_money_24),
                                        contentDescription = "Localized description",
                                        modifier = Modifier.size(ButtonDefaults.IconSize),
                                        tint = Color.White
                                    )
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text("Repay", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to access database", Toast.LENGTH_SHORT).show()
            }
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repaymentRecyclerView = binding.recycleViewRepayment
        repaymentRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        repaymentRecyclerView.layoutManager
        loadingRepayment = binding.LoadingRepay
        repaymentList = arrayListOf<BorrowerActivity.RepaymentHistory>()

        getRepaymentHistory()


    }

    private fun getRepaymentHistory(){
        //initialise database
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        repaymentRecyclerView.visibility = View.GONE
        loadingRepayment.visibility = View.VISIBLE

        databaseRef = FirebaseDatabase.getInstance().getReference("RepaymentHistory").child(auth.currentUser!!.uid)
        
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                repaymentList.clear()
                if (snapshot.exists()) {
                    for (transactionSnap in snapshot.children) {
                        val transactionData =
                            transactionSnap.getValue(BorrowerActivity.RepaymentHistory::class.java)
                        repaymentList.add(transactionData!!)
                    }

                    val tAdapter = RepaymentAdapter(repaymentList)
                    repaymentRecyclerView.adapter = tAdapter

                    repaymentRecyclerView.visibility = View.VISIBLE
                    loadingRepayment.visibility = View.GONE
                }else{
                    repaymentRecyclerView.visibility = View.GONE
                    loadingRepayment.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to access database", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getRepayAmount(callback: (Double?) -> Unit){
        //initialise database
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("Loans")

        databaseRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val loan = snapshot.getValue(BorrowerActivity.BorrowRequest::class.java)
                val amount = (loan?.loanAmount)?.toDouble()
                callback(amount)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to get Wallet data", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}