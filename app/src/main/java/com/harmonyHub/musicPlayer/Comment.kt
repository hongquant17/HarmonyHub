package com.harmonyHub.musicPlayer

class Comment {
    var imageUrl: String? = null
    var name: String? = null
    var comment: String? = null
    var songId: String? = null

    constructor()
    constructor(
        imageUrl: String? = null,
        name: String? = null,
        comment: String? = null,
        songId: String? = null
    ) {

        this.imageUrl = imageUrl
        this.name = name
        this.comment = comment
        this.songId = songId
    }

}