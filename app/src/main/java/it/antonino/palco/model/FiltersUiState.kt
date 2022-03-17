package it.antonino.palco.model

data class FiltersUiState(
    val isFilledConcerts: Boolean = false,
    val concertsItems: ArrayList<Concerto?>?
)

val FiltersUiState.canBookmarkItems: Boolean get() = isFilledConcerts