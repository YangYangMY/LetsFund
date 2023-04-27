package my.edu.tarc.letsfund.ui.borrower.borrowerprofile

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
import java.io.File


class BorrowerProfileFragment : Fragment() {


    private var _binding: FragmentBorrowerprofileBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var database: FirebaseDatabase

    private lateinit var user: Users
    private lateinit var uid: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            binding.imageProfile.setImageURI(uri)
        }
    }

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


        //If the current user ID is valid, retrieve the data
        if(uid.isNotEmpty()) {
            getUserData()
        }

        //Display Profile Photo
        readProfilePhoto()

        // Click to edit the profile
        binding.btnEditProfile.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_borrowerprofile_to_navigation_editprofile)
        }

        // Click to do card payment (Testing purpose) ***
        binding.btnCustomerService.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_borrowerprofile_to_navigation_cardpayment)
        }

        binding.btnLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "You have logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getUserData() {

        databaseRef.child(uid).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(Users::class.java)!!
                binding.textViewEmail.setText(user.email)
                binding.textViewRole.setText(user.role)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to get User Profile data", Toast.LENGTH_SHORT).show()
                binding.textViewEmail.setText("Email")
                binding.textViewRole.setText("Role")
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

            binding.imageProfile.setImageResource(R.drawable.profile)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}