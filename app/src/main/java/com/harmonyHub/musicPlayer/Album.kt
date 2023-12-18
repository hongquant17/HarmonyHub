package com.harmonyHub.musicPlayer

class Album {
    var title: String? = null
    var coverImageUrl: String? = null


    constructor()
    constructor(
        title: String?,
        coverImageUrl: String?,
    ) {
        this.title = title
        this.coverImageUrl = coverImageUrl
    }
}