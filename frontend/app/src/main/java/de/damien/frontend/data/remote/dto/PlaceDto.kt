package de.damien.frontend.data.remote.dto

import de.damien.frontend.domain.models.Place
import java.util.stream.Collectors

data class PlaceDto(
    val comments: List<CommentDto>,
    val creatorDto: AccountDto,
    val description: String,
    val image: ImageDto,
    val latitude: Double,
    val longitude: Double,
    val placeId: Int,
    val title: String
)

fun PlaceDto.toPlace(): Place {
    return Place(
        comments = comments.stream()
            .map { e -> e.toComment() }
            .collect(Collectors.toList()),
        creator = creatorDto.toAccount(),
        description = description,
        image = image.toImage(),
        latitude = latitude,
        longitude = longitude,
        placeId = placeId,
        title = title
    )
}