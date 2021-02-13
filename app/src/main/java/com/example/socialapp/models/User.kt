package com.example.socialapp.models

class User {
    var userId: String? = null
    var name: String? = null
    var nick: String? = null
    var image: String? = null

    constructor() {

    }

    constructor(userId: String, name: String, nick: String, image: String) {
        this.userId = userId
        this.name = name
        this.nick = nick
        this.image = image
    }
}