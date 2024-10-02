package com.example.playlist_maker.search.ui.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class SearchError(
    @DrawableRes val icon: Int,
    @StringRes val text: Int,
    val showRefreshButton: Boolean
)