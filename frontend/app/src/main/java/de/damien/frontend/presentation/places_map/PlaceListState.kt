package de.damien.frontend.presentation.places_map

import de.damien.frontend.domain.models.Place

data class PlaceListState(
    val isLoading: Boolean = false,
    val places: List<Place> = emptyList(),
    val error: String = ""
)
