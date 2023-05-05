package my.edu.tarc.letsfund.ui.lender.wallet

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
import my.edu.tarc.letsfund.databinding.FragmentWalletBinding
import my.edu.tarc.letsfund.ui.lender.LenderActivity

class WalletFragment : Fragment() {
    private lateinit var composeView: ComposeView

    private var _binding: FragmentWalletBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Color
    val light_green = Color(0xFF599D42)
    val light_green_500 = Color(0xFF8BC34A)
    val light_orange = Color(0xFFF6BD60)

    //Database Initialise
    private lateinit var uid: String
    private lateinit var databaseRef : DatabaseReference
    private lateinit var auth: FirebaseAuth

    //recycle view
    private lateinit var transactionRecyclerView: RecyclerView
    private lateinit var loadingWallet : ProgressBar
    private lateinit var transactionList : ArrayList<LenderActivity.PaymentHistory>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        val root: View = binding.root



        composeView = binding.walletbox
        composeView.setContent {
            val walletamount = remember { mutableStateOf<Double?>(null) }
            getWalletAmount { amount ->
                walletamount.value = amount
            }
            Card() {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(light_orange)) {
                    Text(
                        "LetsFund Wallet",Modifier.padding(all = 20.dp),
                        color = Color.White, fontSize = 17.sp, fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        "RM ${walletamount.value?: 0.0}",
                        Modifier
                            .padding(top = 50.dp)
                            .padding(horizontal = 19.dp),
                        fontWeight = FontWeight.Bold, color = Color.White,
                        fontSize = 22.sp, fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }

        composeView = binding.TopUp
        composeView.setContent {
            Button(
                onClick = {

                    findNavController().navigate(R.id.action_navigation_wallet_to_navigation_cardpayment)
                    //val intent = Intent(context, CardPaymentActivity::class.java)
                    //startActivity(intent)

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
                Text("Top Up",  color = Color.White)
            }
        }


        composeView = binding.Withdraw
        composeView.setContent {
            Button(
                onClick = {
                    findNavController().navigate(R.id.action_navigation_wallet_to_navigation_withdraw)
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
                Text("Withdraw",  color = Color.White)
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionRecyclerView = binding.recycleViewTransaction
        transactionRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        transactionRecyclerView.layoutManager
        loadingWallet = binding.LoadingWallet
        transactionList = arrayListOf<LenderActivity.PaymentHistory>()

        getTransactionHistory()
    }

    private fun getTransactionHistory(){
        //initialise database
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        transactionRecyclerView.visibility = View.GONE
        loadingWallet.visibility = View.VISIBLE

        databaseRef = FirebaseDatabase.getInstance().getReference("TransactionHistory").child(auth.currentUser!!.uid)

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                transactionList.clear()
                if (snapshot.exists()) {
                    for (transactionSnap in snapshot.children) {
                        val transactionData =
                            transactionSnap.getValue(LenderActivity.PaymentHistory::class.java)
                        transactionList.add(transactionData!!)
                    }

                    val tAdapter = TransactionAdapter(transactionList)
                    transactionRecyclerView.adapter = tAdapter

                    transactionRecyclerView.visibility = View.VISIBLE
                    binding.scrollViewTransaction.fullScroll(View.FOCUS_UP)
                    loadingWallet.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to access database", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getWalletAmount(callback: (Double?) -> Unit) {

        //initialise database
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("Wallet")

        databaseRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val wallet = snapshot.getValue(LenderActivity.Wallet::class.java)
                val amount = wallet?.walletAmount
                callback(amount)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to get Wallet data", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }
}