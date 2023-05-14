package my.edu.tarc.letsfund.ui.lender.withdraw

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.FragmentWithdrawBinding
import my.edu.tarc.letsfund.ui.lender.LenderActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WithdrawFragment : Fragment() {

    private var _binding: FragmentWithdrawBinding? = null
    private val binding get() = _binding!!
    private var selectedWithdrawOptionCheck: Int = 0


    //Initialise Database
    private lateinit var uid: String
    private lateinit var databaseRef : DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    //Initialise Builder Dialog
    private lateinit var builder : AlertDialog.Builder

    //FOR CONTAINER Withdraw
    private lateinit var withdrawContainer: TextInputLayout
    private lateinit var withdrawAmount: TextInputEditText
    private var finalwithdrawAmount : Double = 0.0

    //For Withdraw Function
    private var WithdrawPass = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // Dialog to enter email for reset password
        var builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Enter Withdraw Amount")

        // Inflate the custom_dialog view
        val viewTopUp = layoutInflater.inflate(R.layout.dialog_withdraw, null)
        withdrawContainer = viewTopUp.findViewById(R.id.withdrawcontainer)
        withdrawAmount = viewTopUp.findViewById(R.id.editTextwithdrawAmount)
        val submit = viewTopUp.findViewById<Button>(R.id.btnwithdrawSubmit)

        binding.spinnerWithdrawOption.visibility = View.INVISIBLE
        binding.textViewWithdrawOption.visibility = View.INVISIBLE
        builder.setView(viewTopUp)
        builder.setCancelable(false) // prevent user from clicking outside of the dialog
        val dialog = builder.create()


        dialog.show()

        // Click to type withdrawal amount
        submit.setOnClickListener{
            var walletAmount: Double = 0.0
            //Output of withdraw input
            val withdrawAmountText = withdrawAmount.text.toString()
            getWalletAmount{amount ->
                if (amount != null) {
                    walletAmount = amount
                }

            if (withdrawAmountText.isNotEmpty()) {
                finalwithdrawAmount = withdrawAmountText.toDouble()
            }
            if(finalwithdrawAmount!! > walletAmount){
                Toast.makeText(context, "Not enough Balance", Toast.LENGTH_SHORT).show()
            }
            else if ( finalwithdrawAmount!! <= 0) {
                Toast.makeText(context, "Please Enter Valid Amount", Toast.LENGTH_SHORT).show()
            }else{
                dialog.dismiss()
                binding.spinnerWithdrawOption.visibility = View.VISIBLE
                binding.textViewWithdrawOption.visibility = View.VISIBLE
            }
            }
        }



        binding.spinnerWithdrawOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            @SuppressLint("ResourceAsColor")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0){
                    //Reset
                    selectedWithdrawOptionCheck = 0
                    binding.editTextMobileNum.visibility = View.INVISIBLE
                    binding.textViewMobileNo.visibility = View.INVISIBLE
                    binding.textViewBank.visibility = View.INVISIBLE
                    binding.spinnerBank.visibility = View.INVISIBLE
                    binding.editTextBankAcc.visibility = View.INVISIBLE
                    binding.textViewAccNo.visibility = View.INVISIBLE
                    binding.btnWithdraw1.visibility = View.VISIBLE
                    binding.btnWithdraw2.visibility = View.GONE
                    binding.spinnerBank.setSelection(0)
                    binding.editTextBankAcc.setText("")
                    binding.editTextMobileNum.setText("")
                    binding.textViewErrorMobile.visibility = View.INVISIBLE
                    binding.textViewErrorMobile.setText("")
                    binding.textViewErrorBank.visibility = View.INVISIBLE
                    binding.textViewErrorBank.setText("")
                    binding.textViewErrorBankAcc.visibility = View.INVISIBLE
                    binding.textViewErrorBankAcc.setText("")
                }
                else if (position == 1){
                    // Reset
                    selectedWithdrawOptionCheck = 1
                    binding.editTextMobileNum.visibility = View.VISIBLE
                    binding.textViewMobileNo.visibility = View.VISIBLE
                    binding.textViewBank.visibility = View.INVISIBLE
                    binding.spinnerBank.visibility = View.INVISIBLE
                   binding.editTextBankAcc.visibility = View.INVISIBLE
                   binding.textViewAccNo.visibility = View.INVISIBLE
                    binding.btnWithdraw1.visibility = View.GONE
                    binding.btnWithdraw2.visibility = View.VISIBLE
                    binding.spinnerBank.setSelection(0)
                    binding.editTextBankAcc.setText("")
                    binding.textViewErrorMobile.visibility = View.VISIBLE
                    binding.textViewErrorBank.visibility = View.INVISIBLE
                    binding.textViewErrorBankAcc.visibility = View.INVISIBLE
                    binding.textViewErrorBank.setText("")
                    binding.textViewErrorBankAcc.setText("")
                }
                else if (position == 2){
                    // Reset
                    selectedWithdrawOptionCheck = 2
                    binding.editTextMobileNum.visibility = View.INVISIBLE
                    binding.textViewMobileNo.visibility = View.INVISIBLE
                    binding.textViewBank.visibility = View.VISIBLE
                    binding.spinnerBank.visibility = View.VISIBLE
                    binding.editTextMobileNum.setText("")
                    binding.textViewErrorBank.visibility = View.VISIBLE
                    binding.textViewErrorBankAcc.visibility = View.VISIBLE
                    binding.textViewErrorMobile.visibility = View.INVISIBLE
                    binding.textViewErrorMobile.setText("")
                    binding.btnWithdraw1.visibility = View.VISIBLE
                    binding.btnWithdraw2.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.btnWithdraw1.visibility = View.VISIBLE
                binding.btnWithdraw2.visibility = View.GONE
            }
        }
        binding.spinnerBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position == 0){
                    binding.editTextBankAcc.setText("")
                    binding.textViewAccNo.visibility = View.INVISIBLE
                    binding.editTextBankAcc.visibility = View.INVISIBLE
                    binding.textViewErrorBank.setText("")
                    binding.textViewErrorBankAcc.setText("")
                    binding.btnWithdraw1.visibility = View.VISIBLE
                    binding.btnWithdraw2.visibility = View.GONE
                }
                else if(position == 1){
                    binding.editTextBankAcc.filters = arrayOf(InputFilter.LengthFilter(10))
                    binding.editTextBankAcc.setText("")
                    binding.textViewAccNo.visibility = View.VISIBLE
                    binding.editTextBankAcc.visibility = View.VISIBLE
                    binding.textViewErrorBankAcc.setText("")
                    binding.btnWithdraw1.visibility = View.GONE
                    binding.btnWithdraw2.visibility = View.VISIBLE
                }
                else if (position == 2){
                    binding.editTextBankAcc.filters = arrayOf(InputFilter.LengthFilter(12))
                    binding.editTextBankAcc.setText("")
                    binding.textViewAccNo.visibility = View.VISIBLE
                    binding.editTextBankAcc.visibility = View.VISIBLE
                    binding.textViewErrorBankAcc.setText("")
                    binding.btnWithdraw1.visibility = View.GONE
                    binding.btnWithdraw2.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //No Action
            }

        }

        binding.btnWithdraw2.setOnClickListener {
            if(selectedWithdrawOptionCheck == 1){
                binding.textViewErrorMobile.text = validPhone()
            }else if(selectedWithdrawOptionCheck == 2){
                binding.textViewErrorBank.text = validBankSelect()
                if(binding.spinnerBank.selectedItemPosition == 1 || binding.spinnerBank.selectedItemPosition == 2){
                    binding.textViewErrorBankAcc.text = validBankAcc()
                }
            }
            if(WithdrawPass){
                //initialise database
                auth = FirebaseAuth.getInstance()
                uid = auth.currentUser?.uid.toString()
                database = FirebaseDatabase.getInstance()

                val today = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val formattedDate = (today.format(formatter)).toString()

                getTransactionNumber { transactionNumber ->
                    if (transactionNumber != -1) {

                        val historyid = transactionNumber.toString()
                        val databaseRefwithdraw = database.reference.child("TransactionHistory")
                            .child(auth.currentUser!!.uid).child(historyid)
                        val WithdrawTransaction: LenderActivity.PaymentHistory =
                            LenderActivity.PaymentHistory(formattedDate, "Withdraw", finalwithdrawAmount)

                        databaseRef = FirebaseDatabase.getInstance().getReference("Wallet")

                        //Retrieve current wallet amount
                        getWalletAmount { currentamount ->
                            val amount: Double? = currentamount?.minus(finalwithdrawAmount)
                            val wallet = mapOf<String, Double?>(
                                "walletAmount" to amount)

                            databaseRefwithdraw.setValue(WithdrawTransaction).addOnCompleteListener {
                                databaseRef.child(uid).updateChildren(wallet).addOnSuccessListener {
                                    val builder1 = AlertDialog.Builder(requireContext())

                                    builder1.setTitle("Withdraw Message")
                                        .setMessage("Your withdraw is successful, your wallet amount is updated")
                                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                                            findNavController().navigate(R.id.action_navigation_withdraw_to_navigation_wallet)
                                        }
                                    builder1.create().show()
                                }
                            }.addOnFailureListener{

                            //Toast.makeText(context, "Payment is failed, please try again", Toast.LENGTH_SHORT).show()
                                val builder2 = AlertDialog.Builder(requireContext())

                                builder2.setTitle("Withdraw Message")
                                .setMessage("Your withdraw is failed, please try again")
                                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                                    findNavController().navigate(R.id.action_navigation_withdraw_to_navigation_wallet)
                                }

                                builder2.create().show()
                            }
                        }
                    }else{
                        Toast.makeText(context, "Failed to access database, please try again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return root
    }

    private fun validBankAcc(): String? {
        val bankAccNum = binding.editTextBankAcc.text.toString()
        val selectOption = binding.spinnerBank.selectedItemPosition
        if(bankAccNum.isEmpty()){
            WithdrawPass = false
            return "Required"

        }
        if(selectOption == 1){
            if(bankAccNum.length != 10){
                WithdrawPass = false
                return "Must be 10 Digits"
            }
        }else if(selectOption == 2){
            if(bankAccNum.length != 12){
                WithdrawPass = false
                return "Must be 12 Digits"
            }
        }
        WithdrawPass = true
        return null
    }

    private fun validPhone(): String? {
        val phoneText = binding.editTextMobileNum.text.toString()
        if (phoneText.isEmpty()) {
            WithdrawPass = false
            return "Required"
        }
        if (!phoneText.matches(".*[0-9].*".toRegex())) {
            WithdrawPass = false
            return "Must be all Digits"
        }
        if(phoneText.length != 10) {
            WithdrawPass = false
            return "Must be 10 Digits"
        }
        WithdrawPass = true
        return null
    }

    private fun validBankSelect(): String? {
        val selectOption = binding.spinnerBank.selectedItemPosition

        if(selectOption == 0){
            WithdrawPass = false
            return "Please select a bank"
        }
        return null
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

}