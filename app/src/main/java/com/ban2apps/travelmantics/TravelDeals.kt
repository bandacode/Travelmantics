package com.ban2apps.travelmantics

data class TravelDeals(
        val title: String,
        val price: String,
        val description: String,
        val id: String = "",
        val imageUrl: String? = null) {
    constructor():this("", "", "")
}