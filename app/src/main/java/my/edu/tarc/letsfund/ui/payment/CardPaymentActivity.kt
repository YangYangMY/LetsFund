package my.edu.tarc.letsfund.ui.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.ActivityCardPaymentBinding
import my.edu.tarc.letsfund.ui.authentication.Users
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity
import my.edu.tarc.letsfund.ui.lender.LenderActivity
import java.util.Calendar

class CardPaymentActivity : AppCompatActivity() {

    //Initialize Binding
    private lateinit var binding: ActivityCardPaymentBinding

    //Initialize Calendar
    private var current = ""
    private val mmyyyy = "MMYYYY"
    private val cal: Calendar = Calendar.getInstance()

    //Initialise Database
    private lateinit var uid: String
    private lateinit var databaseRef : DatabaseReference
    private lateinit var auth: FirebaseAuth

    //Initialise Builder Dialog
    private lateinit var builder : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_payment)

        binding = ActivityCardPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Check the textField changes status
        focusListener()

        //Exp Date input TextChanged
        binding.editTextCardExpDate.addTextChangedListener(textWatcher)


        //Proceed Payment
        binding.btnPay?.setOnClickListener {
            submitForm()
        }

        //Reset Card
        binding.btnReset.setOnClickListener {
            resetCardInput()
        }

        //Click to previous page
        binding.btnPrevious.setOnClickListener {

            getRole { role ->
                if (role.equals("Lender")) {
                    val intent = Intent(this, LenderActivity::class.java)
                    startActivity(intent)
                } else if (role.equals("Borrower")) {
                    val intent = Intent(this, BorrowerActivity::class.java)
                    startActivity(intent)
                }
            }

        }

    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!s.toString().equals(current)) {
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
                    var year = clean.substring(2, 6).toInt()

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

        databaseRef = FirebaseDatabase.getInstance().getReference("Wallet")

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

        var amount : Double? = 100.00
        val wallet = mapOf<String, Double?>(
            "walletAmount" to amount
        )

        if (validCardHolder && validCardNumber && validCardExpDate && validCardCVV) {

            databaseRef.child(uid).updateChildren(wallet).addOnSuccessListener {

                builder = AlertDialog.Builder(this)

                builder.setTitle("Payment Message")
                    .setMessage("Your payment is successful, your wallet amount is updated")
                    .setPositiveButton(getString(R.string.ok),{_,_ ->
                        finish()
                        val intent = Intent(this, LenderActivity::class.java)
                        startActivity(intent)
                    })

                builder.create().show()

            }.addOnFailureListener{
                Toast.makeText(this, "Your payment is failed, please try again", Toast.LENGTH_SHORT).show()
            }


        }else {
            Toast.makeText(this, "Please enter valid input", Toast.LENGTH_SHORT).show()
        }

    }

    private fun focusListener() {

        //Card Holder Name
        binding.editTextCardHolderName?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cardHolderNameContainer?.helperText = validCardHolderName()
            }
        }

        //Card Number
        binding.editTextCardNumber?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cardNumberContainer?.helperText = validCardNumber()
            }
        }

        //Card Expire Date
        binding.editTextCardExpDate?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cardExpDateContainer?.helperText = validCardExpDate()
            }
        }

        //Card CVV
        binding.editTextCVV?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cvvContainer?.helperText = validCVV()
            }
        }


    }

    private fun validCardHolderName(): String? {
        val cardHolderNameText = binding.editTextCardHolderName?.text.toString()
        if (cardHolderNameText.isEmpty()) {
            return "Required"
        }
        return null
    }

    private fun validCardNumber(): String? {
        val cardNumberText = binding.editTextCardNumber?.text.toString()
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
        val expDateText = binding.editTextCardExpDate?.text.toString()
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
        val cvvText = binding.editTextCVV?.text.toString()
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

    private fun getRole(callback: (String?) -> Unit) {

        //initialise database
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        databaseRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                val currentUserRole = user?.role
                callback(currentUserRole)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CardPaymentActivity, "Failed to get User Profile data", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    override fun onBackPressed() {

        getRole { role ->
            if (role.equals("Lender")) {
                val intent = Intent(this, LenderActivity::class.java)
                startActivity(intent)
            } else if (role.equals("Borrower")) {
                val intent = Intent(this, BorrowerActivity::class.java)
                startActivity(intent)
            }
        }

    }
}