package my.edu.tarc.letsfund.ui.borrower.borrowerprofile

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.FragmentBorrowerprofileBinding
import my.edu.tarc.letsfund.ui.authentication.Users
import my.edu.tarc.letsfund.ui.authentication.login.LoginActivity
import my.edu.tarc.letsfund.ui.authentication.profile.EditProfileActivity
import java.io.File


class BorrowerProfileFragment : Fragment() {

    private var _binding: FragmentBorrowerprofileBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var database: FirebaseDatabase

    //Authentication User
    private lateinit var user: Users
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
        val borrowerProfileViewModel =
            ViewModelProvider(this).get(BorrowerProfileViewModel::class.java)

        _binding = FragmentBorrowerprofileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.profile
        borrowerProfileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialise Firebase
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        //Display email info
        getUserEmail { email ->
            binding.textViewEmail.text = email
        }

        //Display Profile Photo
        readProfilePhoto()

        // Click to edit the profile
        binding.btnEditProfile.setOnClickListener{

            //val intent = Intent(context, EditProfileActivity::class.java)
            //startActivity(intent)
            findNavController().navigate(R.id.action_navigation_borrowerprofile_to_navigation_editprofile)

        }

        // Click to logout
        binding.btnLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "You have logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }

        // Dialog to enter email for reset password
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Enter Email to reset Password")

        // Inflate the custom_dialog view
        val view = layoutInflater.inflate(R.layout.custom_dialog, null)
        emailContainer = view.findViewById(R.id.emailContainerForResetPassword)
        email = view.findViewById(R.id.editTextEmailForResetPassword)
        val submit = view.findViewById<Button>(R.id.btnSubmit)

        builder.setView(view)
        val dialog = builder.create()

        // Email Focus Listener
        emailFocusListener()

        // Click to display dialog
        binding.btnChangePassword.setOnClickListener {
            dialog.show()
        }

        // Click to submit email to change the password
        submit.setOnClickListener{

            //Output of email input
            val emailInput = email.text.toString()

            //Output of helperText
            emailContainer.helperText = validEmail()

            //Check if the email helperText is null
            if (emailContainer.helperText == null) {

                auth.sendPasswordResetEmail(emailInput).addOnSuccessListener {
                    Toast.makeText(context, "Please check your email", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }.addOnFailureListener {
                    Toast.makeText(context, "This email is invalid, please try again", Toast.LENGTH_SHORT).show()
                }

            }

        }

    }

    private fun getUserEmail(callback: (String?) -> Unit) {

        databaseRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                val email = user?.email
                callback(email)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to get user email", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    private fun readProfilePhoto() {

        val storageRef = FirebaseStorage.getInstance().reference.child("Profile/$uid")

        val localFile = File.createTempFile("tempImage", "png")
        storageRef.getFile(localFile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.imageProfile.setImageBitmap(bitmap)

        }.addOnFailureListener {

            binding.imageProfile.setImageResource(R.drawable.baseline_account_circle_24)
        }
    }

    private fun emailFocusListener() {
        //Email
        email.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                emailContainer.helperText = validEmail()
            }
        }
    }


    private fun validEmail(): String? {
        val emailText = email.text.toString()
        if (emailText.isEmpty()) {
            return "Required"
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid Email Address"
        }
        return null
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}