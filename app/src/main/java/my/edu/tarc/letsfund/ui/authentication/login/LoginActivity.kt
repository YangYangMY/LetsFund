package my.edu.tarc.letsfund.ui.authentication.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.ActivityLoginBinding
import my.edu.tarc.letsfund.ui.authentication.signup.SignUpActivity
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialise Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


        emailFocusListener()
        passwordFocusListener()

        //Click to Login
        binding.btnLogin.setOnClickListener {
            submitForm()
        }

        //Navigate to Sign Up Page
        binding.navSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }


    private fun submitForm() {

        //Sign Up Progress Bar
        binding.loadingLogin.visibility = View.VISIBLE
        binding.loadingLogin.bringToFront()

        //Output of login input
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        //Output of helperText
        binding.emailContainer.helperText = validEmail()
        binding.passwordContainer.helperText = validPassword()

        //Check if the helperText is null
        val validEmail = binding.emailContainer.helperText == null
        val validPassword = binding.passwordContainer.helperText == null

        //Login Authentication
        if (validEmail && validPassword) {

            val userEmail = FirebaseAuth.getInstance().currentUser?.email

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(this, "Welcome, $userEmail", Toast.LENGTH_SHORT).show()
                    binding.loadingLogin.visibility = View.GONE
                    val intent = Intent(this, BorrowerActivity::class.java)
                    startActivity(intent)
                }else {
                    Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                    binding.loadingLogin.visibility = View.GONE
                    resetLoginInput()
                }
            }
        }else {
            Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
            binding.loadingLogin.visibility = View.GONE
        }

    }

    private fun emailFocusListener() {

        binding.editTextEmail?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.emailContainer?.helperText = validEmail()
            }
        }

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

    private fun passwordFocusListener() {

        binding.editTextPassword?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.passwordContainer?.helperText = validPassword()
            }
        }

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

    private fun resetLoginInput() {
        binding.editTextEmail.setText("")
        binding.editTextPassword.setText("")

        binding.emailContainer.helperText = ""
        binding.passwordContainer.helperText = ""

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}