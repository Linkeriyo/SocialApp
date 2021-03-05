package com.example.socialapp.models

class User {

    var uid = ""
    var name = ""
    var nick = ""
    var image = ""

    constructor() {
    }

    constructor(userId: String, name: String, nick: String, image: String) {
        this.uid = userId
        this.name = name
        this.nick = nick
        this.image = image
    }
}