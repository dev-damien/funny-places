package de.damien.frontend.recyclerviews.place

data class Place(
    val id: String,
    var title: String,
    val creator: String,
    var description: String,
    var imageUrl: String,

    val latitude: Double,
    val longitude: Double,

    var isSelected:Boolean = false
)