package com.harmonyHub.musicPlayer

class Song {
    var songName: String? = null
    var songUrl: String? = null
    var imageUrl: String? = null
    var songArtist: String? = null
    var songDuration: String? = null

    constructor()
    constructor(
        songName: String?,
        songUrl: String?,
        imageUrl: String?,
        songArtist: String?,
        songDuration: String?
    ) {
        this.songName = songName
        this.songUrl = songUrl
        this.imageUrl = imageUrl
        this.songArtist = songArtist
        this.songDuration = songDuration
    }
}