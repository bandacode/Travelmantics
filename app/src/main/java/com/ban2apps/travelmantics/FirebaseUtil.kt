package com.ban2apps.travelmantics

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseUtil {

    companion object {
        private val TAG = FirebaseUtil::class.java.simpleName
    }

    private val db = FirebaseFirestore.getInstance()

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

    fun delete(deal: TravelDeal) {
        db.collection("deals").document(deal.id)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }
}