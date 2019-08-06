package com.ban2apps.travelmantics

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_deal.view.*

class DealAdapter(private val listener: (TravelDeal) -> Unit) :
    RecyclerView.Adapter<DealAdapter.ViewHolder>() {

    companion object {
        private val TAG = DealAdapter::class.java.simpleName
    }

    private val deals = arrayListOf<TravelDeal>()
    private val db = FirebaseFirestore.getInstance()

    init {
        db.collection("deals")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    deals.clear()
                    notifyDataSetChanged()
                    for (document in snapshot.documents) {
                        val deal = document.toObject(TravelDeal::class.java)
                        deal!!.id = document.id
                        deals.add(deal)
                        notifyItemInserted(deals.size - 1)
                    }
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_deal, parent, false))
    }

    override fun getItemCount() = deals.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(deals[position], listener)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(deal: TravelDeal, listener: (TravelDeal) -> Unit) = with(itemView) {
            tvTitle.text = deal.title
            tvDescription.text = deal.description
            val price = "K${deal.price}"
            tvPrice.text = price
            Picasso.get().load(deal.imageUrl)
                .into(image)
            setOnClickListener { listener(deal) }
        }
    }
}