package my.edu.tarc.letsfund.ui.authentication.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import my.edu.tarc.letsfund.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.edu.tarc.letsfund.databinding.FragmentEditProfileBinding
import my.edu.tarc.letsfund.ui.authentication.Users
import my.edu.tarc.letsfund.ui.authentication.signup.RoleActivity
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity
import my.edu.tarc.letsfund.ui.lender.LenderActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null

    //Initialize Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var storageReference: StorageReference

    //Initialize Authentication User
    private lateinit var user: Users
    private lateinit var uid: String

    private lateinit var uri: Uri


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /*
    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            binding.imageProfile.setImageURI(uri)
        }
    }
    */


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        //Initialise Firebase
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        storageReference = FirebaseStorage.getInstance().getReference("Profile/"+auth.currentUser?.uid)
        databaseRef = FirebaseDatabase.getInstance().getReference("users")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Display User Existing Profile
        getUserData()

        //Display Profile Photo
        readProfilePhoto()

        //Setting up DatePicker for Date Of Birth
        dobSelected()

        //Check the textField changes status
        focusListener()

        //Click to reset the data
        binding.btnReset.setOnClickListener{
            getUserData()
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { it ->

                binding.imageProfile.setImageURI(it)
                uri = it!!

            }
        )

        //Select photo from gallery
        binding.imageProfile.setOnClickListener {
            galleryImage.launch("image/*")
        }


        //Click to edit the data
        binding.btnSave.setOnClickListener {
            submitForm()
            uploadImage()
        }


    }

    private fun submitForm() {

        //Role Progress Bar
        binding.loadingProfile.visibility = View.VISIBLE
        binding.loadingProfile.bringToFront()

        //Output of sign up input
        val firstName: String? = binding.editTextFirstName.text.toString()
        val lastName: String? = binding.editTextLastName.text.toString()
        val gender: String? = genderSelected().toString()
        val dob: String? = binding.editTextDob.text.toString()
        val phone: String? = binding.editTextPhone.text.toString()

        //Output of helperText
        binding.firstNameContainer.helperText = validFirstName()
        binding.lastNameContainer.helperText = validLastName()
        binding.phoneContainer.helperText = validPhone()

        //Check if the helperText is null
        val validFirstName = binding.firstNameContainer.helperText == null
        val validLastName = binding.lastNameContainer.helperText == null
        val validPhone = binding.phoneContainer.helperText == null

        val user = mapOf<String, String?>(
            "firstname" to firstName,
            "lastname" to lastName,
            "dob" to dob,
            "gender" to gender,
            "phone" to phone
        )

        //Edit Profile Authentication
        if (validFirstName && validLastName && validPhone) {

            databaseRef.child(uid).updateChildren(user).addOnSuccessListener {

                binding.loadingProfile.visibility = View.GONE
                Toast.makeText(context, "Your profile is updated", Toast.LENGTH_SHORT).show()
                uploadImage()

                getRole { role ->
                    if (role.equals("Lender")) {
                        val intent = Intent(context, LenderActivity::class.java)
                        startActivity(intent)
                    } else if (role.equals("Borrower")) {
                        val intent = Intent(context, BorrowerActivity::class.java)
                        startActivity(intent)
                    }
                }

            }.addOnFailureListener{

                binding.loadingProfile.visibility = View.GONE
                Toast.makeText(context, "Database is failed. Please try again" + auth.currentUser, Toast.LENGTH_SHORT).show()

            }

        } else {
            binding.loadingProfile.visibility = View.GONE
            Toast.makeText(context, "Please enter valid input" + auth.currentUser, Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadImage() {

        storageReference.putFile(uri).addOnSuccessListener { task ->
            task.metadata!!.reference!!.downloadUrl

        }

    }

    private fun getUserData() {
        databaseRef.child(uid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(Users::class.java)!!

                binding.editTextFirstName.setText(user.firstname)
                binding.editTextLastName.setText(user.lastname)
                binding.editTextDob.setText(user.dob)
                getGender(user.gender)
                binding.editTextPhone.setText(user.phone)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to get user profile data", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun readProfilePhoto() {

        storageReference = FirebaseStorage.getInstance().reference.child("Profile/$uid")

        val localFile = File.createTempFile("tempImage", "png")

        storageReference.getFile(localFile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.imageProfile.setImageBitmap(bitmap)

        }.addOnFailureListener {
            binding.imageProfile.setImageResource(R.drawable.baseline_account_circle_24)
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
            val picker = DatePickerDialog(requireContext(), { view, year, monthOfYear, dayOfMonth ->
                binding.editTextDob.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            }, year, month, day)
            picker.show()
        }

    }

    private fun getGender(gender: String?) {

        if (gender == "Male") {
            binding.radioButtonMale.isChecked = true
        }
        if (gender == "Female") {
            binding.radioButtonFemale.isChecked = true

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
                Toast.makeText(context, "Failed to get User Profile data", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}