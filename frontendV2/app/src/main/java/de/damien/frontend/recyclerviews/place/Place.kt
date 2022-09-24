package de.damien.frontend.recyclerviews.place

data class Place(
    val id: String,
    val title: String,
    val creator: String,
    val imageUrl: String,

    val latitude: Double,
    val longitude: Double,

    var isSelected:Boolean = false
)