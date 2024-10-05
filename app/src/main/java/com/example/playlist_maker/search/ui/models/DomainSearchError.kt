package com.example.playlist_maker.search.ui.models

sealed class DomainSearchError {
    object EmptyResult : DomainSearchError()
    object NetworkError : DomainSearchError()
}