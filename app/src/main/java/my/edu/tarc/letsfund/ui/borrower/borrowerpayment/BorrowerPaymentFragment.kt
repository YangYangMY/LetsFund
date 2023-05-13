package my.edu.tarc.letsfund.ui.borrower.borrowerpayment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.FragmentCardPaymentBinding
import my.edu.tarc.letsfund.ui.authentication.Users
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity
import my.edu.tarc.letsfund.ui.lender.LenderActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class BorrowerPaymentFragment : Fragment() {
    //Initialize Binding
    private var _binding: FragmentCardPaymentBinding? = null
    private val binding get() = _binding!!

    //Initialize Calendar
    private var current = ""
    private val mmyyyy = "MMYYYY"
    private val cal: Calendar = Calendar.getInstance()

    //Initialise Database
    private lateinit var uid: String
    private lateinit var databaseRef : DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    //Initialise Builder Dialog
    private lateinit var builder : AlertDialog.Builder

    private lateinit var lenderID : String
    private lateinit var lenderName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Check the textField changes status
        focusListener()

        //Exp Date input TextChanged
        binding.editTextCardExpDate.addTextChangedListener(textWatcher)


        //Proceed Payment
        binding.btnCardPay?.setOnClickListener {
            submitForm()
        }

        //Reset Card
        binding.btnReset.setOnClickListener {
            resetCardInput()
        }
    }


    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.toString() != current) {
                var clean = s.toString().replace("[^\\d.]".toRegex(), "")
                val cleanC = current.replace("[^\\d.]".toRegex(), "")

                val cl = clean.length
                var sel = cl
                for (i in 2..cl step 2) {
                    sel++
                }
                //Fix for pressing delete next to a forward slash
                if (clean == cleanC) sel--

                if (clean.length < 6) {
                    clean += mmyyyy.substring(clean.length)
                } else {
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    val mon = clean.substring(0, 2).toInt()
                    val year = clean.substring(2, 6).toInt()

                    cal.set(Calendar.MONTH, mon - 1)
                    cal.set(Calendar.YEAR, year)

                    val maxMonth = cal.getActualMaximum(Calendar.MONTH) + 1

                    clean = if (mon > maxMonth) {
                        String.format("%02d%02d", maxMonth, year)
                    } else {
                        String.format("%02d%02d", mon, year)
                    }

                }

                clean = String.format("%s/%s", clean.substring(0, 2),
                    clean.substring(2, 6))

                sel = if (sel < 0) 0 else sel
                current = clean
                binding.editTextCardExpDate.setText(current)
                binding.editTextCardExpDate.setSelection(if (sel < current.length) sel else current.length)
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable) {}
    }



    private fun submitForm() {

        //initialise database
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance()

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = (today.format(formatter)).toString()
        getRepayNumber{repayNumber ->
            getRepayAmount { repayAmount ->
                getTransactionNumber { transactionNumber ->
                    if (transactionNumber != -1 && repayNumber != -1) {
                        val transactionID = transactionNumber.toString()
                        val repayID = repayNumber.toString()
                        val databaseRefTransactionHistory =
                            database.reference.child("TransactionHistory").child(auth.currentUser!!.uid)
                                .child(transactionID)
                        val walletTransaction: LenderActivity.PaymentHistory =
                            LenderActivity.PaymentHistory(formattedDate, "Repaid", repayAmount)


                        //Get Lender Name
                        val databaseRefLenderId = database.reference.child("Loans").child(uid)

                        databaseRefLenderId.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    lenderID =
                                        snapshot.getValue(BorrowerActivity.BorrowRequest::class.java)?.lenderID.toString()

                                    val databaseRefLenderName = database.reference.child("users").child(lenderID)
                                    databaseRefLenderId.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                lenderName = snapshot.getValue(Users::class.java)?.firstname.toString()
                                                val databaseRefRepayHistory =
                                                    database.reference.child("RepaymentHistory").child(auth.currentUser!!.uid)
                                                        .child(repayID)
                                                val repayTransaction: BorrowerActivity.RepaymentHistory =
                                                    BorrowerActivity.RepaymentHistory(formattedDate, lenderName, repayAmount)
                                                databaseRefRepayHistory.setValue(repayTransaction).addOnCompleteListener {

                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(context, "Failed to access database", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "Failed to access database", Toast.LENGTH_SHORT).show()
                            }
                        })


                        databaseRef = FirebaseDatabase.getInstance().getReference("Wallet")
                        val databaseRefDeleteRequest = FirebaseDatabase.getInstance().getReference("Loans").child(auth.currentUser!!.uid).removeValue();
                        //Output of helperText
                        binding.cardHolderNameContainer.helperText = validCardHolderName()
                        binding.cardNumberContainer.helperText = validCardNumber()
                        binding.cardExpDateContainer.helperText = validCardExpDate()
                        binding.cvvContainer.helperText = validCVV()

                        //Check if the helperText is null
                        val validCardHolder = binding.cardHolderNameContainer.helperText == null
                        val validCardNumber = binding.cardNumberContainer.helperText == null
                        val validCardExpDate = binding.cardExpDateContainer.helperText == null
                        val validCardCVV = binding.cvvContainer.helperText == null

                        //Retrieve current wallet amount
                        getWalletAmount { currentAmount ->
                            val amount: Double? = repayAmount?.let { currentAmount?.plus(it) }
                            val wallet = mapOf<String, Double?>(
                                "walletAmount" to amount
                            )
                            if (validCardHolder && validCardNumber && validCardExpDate && validCardCVV) {
                                databaseRefDeleteRequest.addOnCompleteListener {
                                    databaseRefTransactionHistory.setValue(walletTransaction).addOnCompleteListener {
                                                databaseRef.child(uid).updateChildren(wallet)
                                                    .addOnSuccessListener {
                                                        builder =
                                                            AlertDialog.Builder(requireContext())
                                                        builder.setTitle("Payment Message")
                                                            .setMessage("Your payment is successful, your wallet amount is updated")
                                                            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                                                                findNavController().navigate(R.id.action_borrowerPaymentFragment_to_navigation_repayment)
                                                            }
                                                        builder.create().show()
                                                    }
                                            }.addOnFailureListener {
                                            //Toast.makeText(context, "Payment is failed, please try again", Toast.LENGTH_SHORT).show()
                                            builder = AlertDialog.Builder(requireContext())
                                            builder.setTitle("Payment Message")
                                                .setMessage("Your payment has failed, please try again")
                                                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                                                    findNavController().navigate(R.id.action_borrowerPaymentFragment_to_navigation_repayment)
                                                }
                                            builder.create().show()
                                        }
                                    }

                                } else {
                                    Toast.makeText(context, "Please enter valid input", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to access database, please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    private fun getRepayNumber(onComplete: (Int) -> Unit) {
        val databaseRefReadTransaction = FirebaseDatabase.getInstance().getReference("RepaymentHistory").child(auth.currentUser!!.uid)
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

    private fun getTransactionNumber(onComplete: (Int) -> Unit) {
        val databaseRefReadTransaction = FirebaseDatabase.getInstance().getReference("TransactionHistory").child(auth.currentUser!!.uid)
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

    private fun focusListener() {

        //Card Holder Name
        binding.editTextCardHolderName.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cardHolderNameContainer.helperText = validCardHolderName()
            }
        }

        //Card Number
        binding.editTextCardNumber.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cardNumberContainer.helperText = validCardNumber()
            }
        }

        //Card Expire Date
        binding.editTextCardExpDate.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cardExpDateContainer.helperText = validCardExpDate()
            }
        }

        //Card CVV
        binding.editTextCVV.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cvvContainer.helperText = validCVV()
            }
        }


    }

    private fun validCardHolderName(): String? {
        val cardHolderNameText = binding.editTextCardHolderName.text.toString()
        if (cardHolderNameText.isEmpty()) {
            return "Required"
        }
        return null
    }

    private fun validCardNumber(): String? {
        val cardNumberText = binding.editTextCardNumber.text.toString()
        if (cardNumberText.isEmpty()) {
            return "Required"
        }
        if (!cardNumberText.matches(".*[0-9].*".toRegex())) {
            return "Must be all Digits"
        }
        if(!cardNumberText.startsWith("4")) {
            return "The card number must start with '4' digit"
        }
        if(cardNumberText.length != 16) {
            return "Must be 16 Digits"
        }
        return null
    }

    private fun validCardExpDate(): String? {
        val expDateText = binding.editTextCardExpDate.text.toString()
        if (expDateText.isEmpty()) {
            return "Required"
        }
        if (!expDateText.substring(0, 2).matches(Regex("\\d+"))) {
            return "Expire Date must be all digits"
        }
        if (!expDateText.substring(3, 7).matches(Regex("\\d+"))) {
            return "Expire Date must be all digits"
        }
        return null
    }

    private fun validCVV(): String? {
        val cvvText = binding.editTextCVV.text.toString()
        if (cvvText.isEmpty()) {
            return "Required"
        }
        if (!cvvText.matches(".*[0-9].*".toRegex())) {
            return "Must be all Digits"
        }
        if(cvvText.length != 3) {
            return "Must be 3 Digits"
        }
        return null
    }

    private fun resetCardInput() {

        //Reset Edit Text
        binding.editTextCardHolderName.setText("")
        binding.editTextCardNumber.setText("")
        binding.editTextCardExpDate.setText("")
        binding.editTextCVV.setText("")

        //Reset HelperText
        binding.cardHolderNameContainer.helperText = ""
        binding.cardNumberContainer.helperText = ""
        binding.cardExpDateContainer.helperText = ""
        binding.cvvContainer.helperText = ""

    }
}