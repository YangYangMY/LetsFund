package my.edu.tarc.letsfund.ui.borrower.applyloan

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.letsfund.databinding.FragmentApplyloanBinding
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.ui.authentication.Users
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity
import java.util.*

class ApplyLoanFragment : Fragment() {

    private var _binding: FragmentApplyloanBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseRefPublicList: DatabaseReference
    private lateinit var databaseRefCheck: DatabaseReference
    private lateinit var databaseRefUpdate:DatabaseReference
    private lateinit var database: FirebaseDatabase


    private lateinit var uid: String

    //Email Details for reset Password
    private lateinit var emailContainer: TextInputLayout
    private lateinit var email: TextInputEditText

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val applyLoanViewModel =
            ViewModelProvider(this).get(ApplyLoanViewModel::class.java)

        _binding = FragmentApplyloanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.applyDashboard
        applyLoanViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialise database
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("Loans")


        getCurrentLoanNumber { LoanNumber ->
            if (LoanNumber != -1) {
                val transactionID = LoanNumber.toString()
                databaseRefPublicList = FirebaseDatabase.getInstance().getReference("LoanLists").child(transactionID)
            }
            val titleInput = view.findViewById<TextInputEditText>(R.id.editTextBorrowerTitle)
            val amountInput = view.findViewById<TextInputEditText>(R.id.editTextBorrowerAmount)
            val descInput = view.findViewById<TextInputEditText>(R.id.editTextBorrowerDesc)
            val submit = view.findViewById<Button>(R.id.borrowerSubmitButton)
            val borrowerId = auth.currentUser!!.uid
            val borrowStatus = "Pending"

            val editText = view.findViewById<TextInputEditText>(R.id.editTextBorrowerDesc)
            val charCount = view.findViewById<TextView>(R.id.charCount)

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    charCount.text = "${s?.length ?: 0}/300"
                }

                override fun afterTextChanged(s: Editable?) {}
            })


            val cal = Calendar.getInstance()  // create a calendar instance
            cal.add(Calendar.MONTH, 1)  // add 1 month to current date
            val nextMonth = cal.time  // get the next month date
            val dateFormat =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())  // create a date formatter
            val nextMonthStr = dateFormat.format(nextMonth)  // format the date to string
            val paymentDate =
                view.findViewById<TextView>(R.id.dateTextView)// display the date in the text view
            paymentDate.text = nextMonthStr

            submit.setOnClickListener {
                val title = titleInput.text.toString()
                val amount = amountInput.text.toString()
                val desc = descInput.text.toString()

                if (amount.isNotEmpty() && desc.isNotEmpty()) {
                    // retrieve user data
                    val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val user = snapshot.getValue(Users::class.java)
                                if (user != null) {
                                    val borrowerName = user.firstname


                                    val loan = BorrowerActivity.BorrowRequest(
                                        loanTitle = title,
                                        loanAmount = amount,
                                        loanDesc = desc,
                                        loanReqEndDate = nextMonthStr,
                                        borrowerName = borrowerName,
                                        borrowerID = borrowerId,
                                        status = borrowStatus
                                    )


                                    // save loan data
                                    databaseRefPublicList.setValue(loan)
                                    databaseRef.child(uid).setValue(loan)
                                    Toast.makeText(
                                        context,
                                        "Loan application submitted",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to retrieve user data",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                context,
                                "Error retrieving user data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    Toast.makeText(
                        context,
                        "Please enter amount and description",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getLoanListNumber(onComplete: (Int) -> Unit) {
        val databaseRefReadTransaction = FirebaseDatabase.getInstance().getReference("LoanLists")
        databaseRefReadTransaction.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val numberOfTransactions = snapshot.childrenCount.toInt()
                val newTransactionNumber = if (numberOfTransactions > 0) numberOfTransactions + 1 else 1
                onComplete(newTransactionNumber)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(-1) // indicates an error occurred
            }
        })
    }

    private fun getCurrentLoanNumber(onComplete: (Int) -> Unit) {
        val databaseRefReadTransaction = FirebaseDatabase.getInstance().getReference("LoanLists")
        databaseRefReadTransaction.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var borrowerIDMatch: String? = null
                var count = 1
                var pass = false

                while (snapshot.hasChild(count.toString())) {
                    val childSnapshot = snapshot.child(count.toString())
                    val borrowerId = childSnapshot.child("borrowerID").getValue(String::class.java)

                    // Check borrower ID
                    if (borrowerId == auth.currentUser?.uid) {
                        borrowerIDMatch = borrowerId
                        pass = true
                        break // Stop iterating once a match is found
                    }else{
                        pass = false
                    }

                    count++
                }
                if(pass){
                    onComplete(count)
                }else{
                    getLoanListNumber { loanListNumber ->
                        onComplete(loanListNumber)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(-1) // indicates an error occurred
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}