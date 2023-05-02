package my.edu.tarc.letsfund.ui.authentication.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.ActivityEditProfileBinding
import my.edu.tarc.letsfund.databinding.FragmentEditProfileBinding
import my.edu.tarc.letsfund.ui.authentication.Users
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.letsfund.databinding.ActivityRoleBinding
import my.edu.tarc.letsfund.databinding.FragmentBorrowerprofileBinding
import my.edu.tarc.letsfund.ui.authentication.signup.SignUpActivity
import my.edu.tarc.letsfund.ui.borrower.BorrowerActivity
import my.edu.tarc.letsfund.ui.borrower.borrowerprofile.BorrowerProfileFragment
import my.edu.tarc.letsfund.ui.lender.LenderActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Calendar
import java.util.TimeZone


class EditProfileActivity : AppCompatActivity() {

    //Initialize Binding
    private lateinit var binding: ActivityEditProfileBinding

    //Initialize Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var storageReference: StorageReference
    private lateinit var uri: Uri

    //Initialize Authentication User
    private lateinit var user: Users
    private lateinit var uid: String

    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            binding.imageProfile.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Initialise Firebase
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        //If the current user ID is valid, retrieve the data
        if(uid.isNotEmpty()) {
            getUserData()

            //Display Profile Photo
            readProfilePhoto()
        }

        binding.imageProfile.setOnClickListener {
            getPhoto.launch("image/*")
        }

        //Setting up DatePicker for Date Of Birth
        dobSelected()

        //Check the textField changes status
        focusListener()

        //Click to reset the data
        binding.btnReset.setOnClickListener{
            getUserData()
        }

        //Click to edit the data
        binding.btnSave.setOnClickListener {
            submitForm()
        }

        //Click to previous page
        binding.btnPrevious?.setOnClickListener {

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


    private fun submitForm() {

        //Role Progress Bar
        binding.loadingProfile.visibility = View.VISIBLE
        binding.loadingProfile.bringToFront()

        //Output of sign up input
        val firstName = binding.editTextFirstName.text.toString()
        val lastName = binding.editTextLastName.text.toString()
        val gender = genderSelected().toString()
        val dob = binding.editTextDob.text.toString()
        val phone = binding.editTextPhone.text.toString()

        //Output of helperText
        binding.firstNameContainer.helperText = validFirstName()
        binding.lastNameContainer.helperText = validLastName()
        binding.phoneContainer.helperText = validPhone()

        //Check if the helperText is null
        val validFirstName = binding.firstNameContainer.helperText == null
        val validLastName = binding.lastNameContainer.helperText == null
        val validPhone = binding.phoneContainer.helperText == null

        val user = mapOf<String, String>(
            "firstname" to firstName,
            "lastname" to lastName,
            "dob" to dob,
            "gender" to gender,
            "phone" to phone,
        )

        //Edit Profile Validation
        if (validFirstName && validLastName && validPhone) {

            databaseRef.child(uid).updateChildren(user).addOnSuccessListener {

                binding.loadingProfile.visibility = View.GONE
                Toast.makeText(this, "Your profile is updated", Toast.LENGTH_SHORT).show()
                uploadProfilePicture()

            }.addOnFailureListener{

                binding.loadingProfile.visibility = View.GONE
                Toast.makeText(this, "Failed to update profile. Try again!" + auth.currentUser, Toast.LENGTH_SHORT).show()

            }


        }else {
            binding.loadingProfile.visibility = View.GONE
            Toast.makeText(this, "Failed to update profile. Try again!" + auth.currentUser, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@EditProfileActivity, "Failed to get User Profile data", Toast.LENGTH_SHORT).show()
            }

        })
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun uploadProfilePicture() {

        val filename = "profile.png"
        val file = Uri.fromFile(File(this.filesDir, filename))

        if (binding.imageProfile.drawable?.constantState != resources.getDrawable(R.drawable.baseline_account_circle_24)?.constantState) {
            storageReference = FirebaseStorage.getInstance().getReference("Profile/"+auth.currentUser?.uid)

            storageReference.putFile(file).addOnFailureListener {
                Toast.makeText(this, "The photo file is invalid", Toast.LENGTH_SHORT).show()
            }

        }

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
            val picker = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
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
                Toast.makeText(this@EditProfileActivity, "Failed to get User Profile data", Toast.LENGTH_SHORT).show()
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