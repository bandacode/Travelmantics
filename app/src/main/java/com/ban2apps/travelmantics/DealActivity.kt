package com.ban2apps.travelmantics

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_deal.*

class DealActivity : AppCompatActivity() {

    private lateinit var mDeal: TravelDeal
    private var isAdmin = false

    companion object {
        //image pick code
        private const val IMAGE_PICK_CODE = 1000
        //Permission code
        private const val PERMISSION_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal)

        val deal = intent.getSerializableExtra("deal") as TravelDeal?
        if (deal != null) {
            txtTitle.setText(deal.title)
            txtDescription.setText(deal.description)
            txtPrice.setText(deal.price)
            mDeal = deal
            showImage(mDeal.imageUrl)
        } else mDeal = TravelDeal()

        isAdmin = getAdmin()
        invalidateOptionsMenu()

        buttonUpload.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    //permission already granted
                    pickImageFromGallery()
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery()
            }
        }
    }

    private fun getAdmin(): Boolean {
        val pref = getSharedPreferences("prefs", 0)
        val enabler = pref.getBoolean("user_id", false)
        enableEditTexts(enabler)
        return enabler
    }

    private fun clean() {
        txtTitle.setText("")
        txtDescription.setText("")
        txtPrice.setText("")
    }

    private fun saveDeal() {
        mDeal.title = txtTitle.text.toString()
        mDeal.description = txtDescription.text.toString()
        mDeal.price = txtPrice.text.toString()
        FirebaseUtil().add(mDeal)
    }

    private fun deleteDeal() {
        if (!::mDeal.isInitialized) {
            Toast.makeText(this, "Cannot delete deal before saving", Toast.LENGTH_LONG).show()
            return
        }
        FirebaseUtil().delete(mDeal)
    }

    private fun enableEditTexts(isEnabled: Boolean) {
        txtDescription.isEnabled = isEnabled
        txtPrice.isEnabled = isEnabled
        txtTitle.isEnabled = isEnabled
        buttonUpload.visibility = if (isEnabled) View.VISIBLE else View.GONE
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(Intent.createChooser(intent, "Insert Picture"), IMAGE_PICK_CODE)
    }

    private fun showImage(url: String?) {
        Picasso.get().load(url)
            .into(dealImage)
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            if (data?.data != null) {
                val imageUri = data.data
                val ref = FirebaseUtil().storageRef.child(imageUri?.lastPathSegment!!)
                val uploadTask = ref.putFile(imageUri)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    val picName = taskSnapshot.storage.path
                    mDeal.imageName = picName
                }.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        mDeal.imageUrl = downloadUri.toString()
                        showImage(mDeal.imageUrl)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_deal, menu)
        menu?.findItem(R.id.action_save)?.isVisible = isAdmin
        menu?.findItem(R.id.action_delete)?.isVisible = isAdmin
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_save -> {
                saveDeal()
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show()
                clean()
                finish()
                true
            }
            R.id.action_delete -> {
                deleteDeal()
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
