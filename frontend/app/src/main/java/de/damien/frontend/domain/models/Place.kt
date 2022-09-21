package de.damien.frontend.domain.models

import de.damien.frontend.data.remote.dto.AccountDto
import de.damien.frontend.data.remote.dto.CommentDto
import de.damien.frontend.data.remote.dto.ImageDto

data class Place(
    val comments: List<Comment>,
    val creator: Account,
    val description: String,
    val image: Image,
    val latitude: Double,
    val longitude: Double,
    val placeId: Int,
    val title: String
)
