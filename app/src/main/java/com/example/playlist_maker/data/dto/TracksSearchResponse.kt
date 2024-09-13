package com.example.playlist_maker.data.dto

class TracksSearchResponse(val resultCount: Int,
                           val results: List<TrackDto>) : Response()
