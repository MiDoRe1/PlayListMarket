package com.example.playlistmarket.TrackModel

data class Track(
    val trackId: Long,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTime: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String) {

    val coverArtWorkUrl : String
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    val year : String
        get() = releaseDate.substringBefore('-')
}