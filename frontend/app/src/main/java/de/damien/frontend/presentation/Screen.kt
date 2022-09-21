package de.damien.frontend.presentation

sealed class Screen(
    val route: String
) {
    object PlaceListScreen : Screen("place_list_screen")
    object PlaceDetailScreen : Screen("place_detail_screen")
}
