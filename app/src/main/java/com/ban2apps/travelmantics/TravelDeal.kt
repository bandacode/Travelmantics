package com.ban2apps.travelmantics

import java.io.Serializable

data class TravelDeal(
        var title: String,
        var price: String,
        var description: String,
        var id: String = "",
        var imageUrl: String? = null) : Serializable {
    constructor():this("", "", "")
}