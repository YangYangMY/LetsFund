package my.edu.tarc.letsfund.ui.authentication.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.ActivityRoleBinding
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity

class RoleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoleBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role)

        binding = ActivityRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialise Firebase
        auth = FirebaseAuth.getInstance()

        var role: String = ""

        binding.radioGroupRole.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.radioButtonLender) {
                Toast.makeText(this, "Lender Role is selected", Toast.LENGTH_SHORT).show()
                binding.btnRole.isEnabled = true
                role = "Lender"
            }
            if(checkedId == R.id.radioButtonBorrower) {
                Toast.makeText(this, "Borrower Role is selected", Toast.LENGTH_SHORT).show()
                binding.btnRole.isEnabled = true
                role = "Borrower"
            }
        }

        binding.btnRole.setOnClickListener {
            submitForm(role)
        }

    }

    private fun submitForm(role: String) {

        //Role Progress Bar
        binding.loadingRole.visibility = View.VISIBLE
        binding.loadingRole.bringToFront()

        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        val user = mapOf<String, String>(
            "role" to role
        )

        databaseRef.child(auth.currentUser!!.uid).updateChildren(user).addOnSuccessListener {

            val userEmail = FirebaseAuth.getInstance().currentUser?.email

            binding.loadingRole.visibility = View.GONE
            Toast.makeText(this, "Welcome, $userEmail", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, BorrowerActivity::class.java)
            startActivity(intent)


        }.addOnFailureListener{

            binding.loadingRole.visibility = View.GONE
            Toast.makeText(this, "Failed to sign up. Try again!" + auth.currentUser, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)

        }

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}