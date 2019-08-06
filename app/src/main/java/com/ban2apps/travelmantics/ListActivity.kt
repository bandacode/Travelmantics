package com.ban2apps.travelmantics

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 123
        private val TAG = ListActivity::class.java.simpleName
        private const val PREFS = "prefs"
        private const val USER_ID = "user_id"
    }

    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        if (auth.currentUser == null)
            signIn()
        else
            FirebaseUtil().checkAdmin(this, auth.currentUser!!.uid)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        dealsList.apply {
            layoutManager = LinearLayoutManager(this@ListActivity)
            adapter = DealAdapter { deal ->
                val intent = Intent(this@ListActivity, DealActivity::class.java)
                intent.putExtra("deal", deal)
                startActivity(intent)
            }
        }
    }

    private fun signIn() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.MyTheme)
                .build(),
            RC_SIGN_IN
        )
    }

    fun showMenu() {
        invalidateOptionsMenu()
    }

    private fun getAdmin(): Boolean {
        val pref = getSharedPreferences(PREFS, 0)
        return pref.getBoolean(USER_ID, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val insertMenu = menu?.findItem(R.id.action_new)
        insertMenu?.isVisible = getAdmin()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_new -> {
                startActivity(Intent(this, DealActivity::class.java))
                true
            }
            R.id.action_logout -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        Log.d(TAG, "User successfully logged out!")
                        FirebaseUtil().attachListener(authListener)
                    }
                FirebaseUtil().detachListener(authListener)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        FirebaseUtil().detachListener(authListener)
    }

    override fun onResume() {
        super.onResume()
        FirebaseUtil().attachListener(authListener)
    }
}
