package my.edu.tarc.letsfund.ui.authentication.signup

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.ActivitySignUpBinding
import my.edu.tarc.letsfund.ui.authentication.Users
import my.edu.tarc.letsfund.ui.authentication.login.LoginActivity
import my.edu.tarc.letsfund.ui.lender.LenderActivity
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialise Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //Setting up DatePicker for Date Of Birth
        dobSelected()

        //Check the textField changes status
        focusListener()

        //Click to Sign Up
        binding.btnSignUp.setOnClickListener {
            submitForm()
        }

        //Navigate to Login Page
        binding.navLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun submitForm() {

        //Sign Up Progress Bar
        binding.loadingSignUp.visibility = View.VISIBLE
        binding.loadingSignUp.bringToFront()

        //Output of sign up input
        val firstName = binding.editTextFirstName.text.toString()
        val lastName = binding.editTextLastName.text.toString()
        val gender = genderSelected().toString()
        val dob = binding.editTextDob.text.toString()
        val phone = binding.editTextPhone.text.toString()
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        //Output of helperText
        binding.firstNameContainer.helperText = validFirstName()
        binding.lastNameContainer.helperText = validLastName()
        binding.phoneContainer.helperText = validPhone()
        binding.emailContainer.helperText = validEmail()
        binding.passwordContainer.helperText = validPassword()

        //Check if the helperText is null
        val validFirstName = binding.firstNameContainer.helperText == null
        val validLastName = binding.lastNameContainer.helperText == null
        val validPhone = binding.phoneContainer.helperText == null
        val validEmail = binding.emailContainer.helperText == null
        val validPassword = binding.passwordContainer.helperText == null

        //Sign Up Authentication
        if (validFirstName && validLastName && validPhone && validEmail && validPassword) {

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful) {
                    val databaseRef = database.reference.child("users").child(auth.currentUser!!.uid)
                    val users: Users = Users(firstName, lastName, dob, gender, phone, email, "")

                    //Create Wallet
                        val databaseRef1 = database.reference.child("Wallet").child(auth.currentUser!!.uid)
                        val paymentHistory: LenderActivity.PaymentHistory = LenderActivity.PaymentHistory(null, null, null)
                        val wallet: LenderActivity.Wallet = LenderActivity.Wallet(0.00)


                        databaseRef1.setValue(wallet).addOnCompleteListener {
                            databaseRef.setValue(users).addOnCompleteListener {
                                if(it.isSuccessful) {
                                    binding.loadingSignUp.visibility = View.GONE
                                    val intent = Intent(this, RoleActivity::class.java)
                                    startActivity(intent)
                                }else {
                                    Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                                    resetSignUpInput()
                                }
                            }
                        }
                }else {
                    Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                    binding.loadingSignUp.visibility = View.GONE
                }
            }
        }else {
            Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
            binding.loadingSignUp.visibility = View.GONE
        }

    }

    private fun focusListener() {

        //First Name
        binding.editTextFirstName?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.firstNameContainer?.helperText = validFirstName()
            }
        }

        //Last Name
        binding.editTextLastName?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.lastNameContainer?.helperText = validLastName()
            }
        }

        //Phone
        binding.editTextPhone?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.phoneContainer?.helperText = validPhone()
            }
        }

        //Email
        binding.editTextEmail?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.emailContainer?.helperText = validEmail()
            }
        }

        //Password
        binding.editTextPassword?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.passwordContainer?.helperText = validPassword()
            }
        }

    }

    private fun dobSelected() {

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"))
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        //Current Date
        binding.editTextDob.setText("$day/${month + 1}/$year")

        //Setting up DatePicker on EditText
        binding.editTextDob.setOnClickListener {

            //Date Picker Dialog
            val picker = DatePickerDialog(this@SignUpActivity, { view, year, monthOfYear, dayOfMonth ->
                binding.editTextDob.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            }, year, month, day)
            picker.show()
        }

    }

    private fun genderSelected(): String? {
        val gender = binding.radioGroupGender.checkedRadioButtonId

        if(gender == binding.radioButtonMale.id) {
            return "Male"
        }else {
            return "Female"
        }
        return null
    }

    private fun validFirstName(): String? {
        val firstNameText = binding.editTextFirstName?.text.toString()
        if (firstNameText.isEmpty()) {
            return "Required"
        }
        return null
    }

    private fun validLastName(): String? {
        val lastNameText = binding.editTextLastName?.text.toString()
        if (lastNameText.isEmpty()) {
            return "Required"
        }
        return null
    }

    private fun validPhone(): String? {
        val phoneText = binding.editTextPhone?.text.toString()
        if (phoneText.isEmpty()) {
            return "Required"
        }
        if (!phoneText.matches(".*[0-9].*".toRegex())) {
            return "Must be all Digits"
        }
        if(phoneText.length != 10) {
            return "Must be 10 Digits"
        }
        return null
    }

    private fun validEmail(): String? {
        val emailText = binding.editTextEmail?.text.toString()
        if (emailText.isEmpty()) {
            return "Required"
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid Email Address"
        }
        return null
    }

    private fun validPassword(): String? {
        val passwordText = binding.editTextPassword?.text.toString()
        if (passwordText.isEmpty()) {
            return "Required"
        }
        if(passwordText.length < 8) {
            return "Minimum 8 Character Password"
        }
        if(!passwordText.matches(".*[A-Z].*".toRegex())) {
            return "Must Contain 1 Upper-case Character"
        }
        if(!passwordText.matches(".*[a-z].*".toRegex())) {
            return "Must Contain 1 Lower-case Character"
        }
        if(!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) {
            return "Must Contain 1 Special Character"
        }
        return null
    }

    private fun resetSignUpInput() {

        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        //Reset Edit Text
        binding.editTextFirstName.setText("")
        binding.editTextLastName.setText("")
        binding.editTextDob.setText("${day + 1}/${month + 1}/$year")
        binding.editTextPhone.setText("")
        binding.editTextEmail.setText("")
        binding.editTextPassword.setText("")

        //Reset HelperText
        binding.firstNameContainer.helperText = ""
        binding.lastNameContainer.helperText = ""
        binding.phoneContainer.helperText = ""
        binding.emailContainer.helperText = ""
        binding.passwordContainer.helperText = ""


    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}