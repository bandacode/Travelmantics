package com.ban2apps.travelmantics

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_deal.*

class DealActivity : AppCompatActivity() {

    private lateinit var mDeal: TravelDeal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal)

        val deal = intent.getSerializableExtra("deal") as TravelDeal?
        if (deal != null) {
            txtTitle.setText(deal.title)
            txtDescription.setText(deal.description)
            txtPrice.setText(deal.price)
            mDeal = deal
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_deal, menu)
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
}
