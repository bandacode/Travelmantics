package com.ban2apps.travelmantics

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_new -> {
                startActivity(Intent(this, DealActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
