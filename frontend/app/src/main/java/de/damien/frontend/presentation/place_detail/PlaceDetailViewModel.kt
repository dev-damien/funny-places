package de.damien.frontend.presentation.place_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.damien.frontend.common.Constants
import de.damien.frontend.common.Resource
import de.damien.frontend.domain.use_cases.get_place.GetPlaceUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    private val getPlaceUseCase: GetPlaceUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(PlaceDetailState())
    val state: State<PlaceDetailState> = _state

    init {
        savedStateHandle.get<Long>(Constants.PARAM_PLACE_ID)?.let { placeId ->
            getPlace(placeId)
        }
    }

    private fun getPlace(placeId: Long) {
        getPlaceUseCase(placeId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = PlaceDetailState(place = result.data)
                }
                is Resource.Error -> {
                    _state.value =
                        PlaceDetailState(error = result.message ?: "An unexpected error occurred")
                }
                is Resource.Loading -> {
                    _state.value = PlaceDetailState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

}