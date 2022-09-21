package de.damien.frontend.presentation.place_detail

import de.damien.frontend.domain.models.Place

data class PlaceDetailState(
    val isLoading: Boolean = false,
    val place: Place? = null,
    val error: String = ""
)
