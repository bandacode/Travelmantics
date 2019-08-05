package com.ban2apps.travelmantics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_insert.*

class InsertActivity : AppCompatActivity() {

    companion object {
        private val TAG = InsertActivity::class.java.simpleName
    }

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_save -> {
                saveDeal()
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show()
                clean()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clean() {
        txtTitle.setText("")
        txtDescription.setText("")
        txtPrice.setText("")
    }

    private fun saveDeal() {
        val title = txtTitle.text.toString()
        val desc = txtDescription.text.toString()
        val price = txtPrice.text.toString()
        val deal = TravelDeals(title, price, desc)
        db.collection("deals")
                .add(deal)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
    }
}
