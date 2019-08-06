package com.ban2apps.travelmantics

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FirebaseUtil {

    companion object {
        private val TAG = FirebaseUtil::class.java.simpleName
    }

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference.child("deals_pics")

    fun add(deal: TravelDeal) {
        if (deal.id == "")
            db.collection("deals")
                .add(deal)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        else
            db.collection("deals").document(deal.id)
                .set(deal)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot edited with ID: ${deal.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
    }

    fun checkAdmin(context: Activity, uid: String) {
        val docRef = db.collection("administrators").document(uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document["name"] != null) {
                    setAdmin(context, true)
                    Log.d(TAG, "User is an Admin")
                } else {
                    setAdmin(context, false)
                    Log.d(TAG, "No such document, not an Admin")
                }
                (context as ListActivity).showMenu()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun setAdmin(context: Context, isAdmin: Boolean) {
        val pref = context.applicationContext.getSharedPreferences("prefs", 0)
        pref.edit {
            putBoolean("user_id", isAdmin)
        }
    }

    fun delete(deal: TravelDeal) {
        db.collection("deals").document(deal.id)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        if (!deal.imageName.isNullOrEmpty()) {
            val storageRef = storage.reference
            val desertRef = storageRef.child(deal.imageName!!)
            desertRef.delete().addOnSuccessListener {
                Log.d(TAG, "Image file successfully deleted!")
            }.addOnFailureListener { e -> Log.w(TAG, "Error deleting file", e) }
        }
    }

    fun attachListener(authListener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(authListener)
    }

    fun detachListener(authListener: FirebaseAuth.AuthStateListener) {
        auth.removeAuthStateListener(authListener)
    }
}