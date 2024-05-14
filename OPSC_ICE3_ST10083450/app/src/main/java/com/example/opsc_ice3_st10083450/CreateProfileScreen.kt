package com.example.opsc_ice3_st10083450

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateProfileScreen : AppCompatActivity() {

    private lateinit var usernameEt: EditText
    private lateinit var qualificationEt: EditText
    private lateinit var studentNumberEt: EditText
    private lateinit var createProfileBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var selectImageBtn: Button
    private lateinit var uploadImageBtn: Button
    private lateinit var imageUri: Uri
    private lateinit var firebaseImage: ImageView
    private var theImageUrl: String? = null
    private lateinit var dbRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile_screen)
        dbRef = FirebaseDatabase.getInstance().getReference("users")

        firebaseImage = findViewById(R.id.firebaseImage)
        usernameEt = findViewById(R.id.etProfUsername)
        qualificationEt = findViewById(R.id.etQualification)
        studentNumberEt = findViewById(R.id.etStudentNumber)
        createProfileBtn = findViewById(R.id.btnCreateProfile)
        cancelBtn = findViewById(R.id.btnCancel)

        createProfileBtn.setOnClickListener{
            saveProfileData()
            var Intent = Intent(this, MainScreen::class.java)
            startActivity(Intent)
        }

        selectImageBtn = findViewById(R.id.btnSelectImage)
        selectImageBtn.setOnClickListener{
            selectImage()
        }

        uploadImageBtn = findViewById(R.id.btnUploadImage)
        uploadImageBtn.setOnClickListener{
            uploadImage()
        }

    }

    private fun selectImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
        } else {

            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    openGallery()
                } else {

                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {

            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
    }
    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)

        // Get the current user's ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid ?: ""

        // Reference the storage location with the user's ID
        val storageReference = FirebaseStorage.getInstance().getReference("images/$userID/$fileName")

        storageReference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                theImageUrl = uri.toString()

                // Clear image view
                firebaseImage.setImageURI(null)

                // Inform the user
                Toast.makeText(this@CreateProfileScreen, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }.addOnFailureListener { exception ->
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this@CreateProfileScreen, "Failed to Upload: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK){

            imageUri = data?.data!!
            firebaseImage.setImageURI(imageUri)
        }
    }
    private fun saveProfileData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // User is not authenticated, redirect to login screen
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
            return
        }

        // Getting values
        val username = usernameEt.text.toString()
        val qualification = qualificationEt.text.toString()
        val studentNumber = studentNumberEt.text.toString()

        if (username.isEmpty() || qualification.isEmpty() || studentNumber.isEmpty()) {
            // Handle empty fields
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userID = currentUser.uid
        val profile = ProfileModel(
            user_ID = userID,
            user_Username = username,
            user_Qualification = qualification,
            user_StudentNumber = studentNumber,
            image_Url = theImageUrl
        )

        dbRef.child(userID).setValue(profile)
            .addOnCompleteListener {
                Toast.makeText(this, "Data Inserted Successfully", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
    }


}

