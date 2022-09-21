package de.damien.frontend.data.remote.dto

import androidx.compose.foundation.Image
import de.damien.frontend.domain.models.Image

data class ImageDto(
    val imageId: Int,
    val imageData: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageDto

        if (!imageData.contentEquals(other.imageData)) return false

        return true
    }

    override fun hashCode(): Int {
        return imageData.contentHashCode()
    }
}

fun ImageDto.toImage(): Image {
    return Image(
        imageId = imageId,
        imageData = imageData
    )
}