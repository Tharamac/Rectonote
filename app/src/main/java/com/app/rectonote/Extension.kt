package com.app.rectonote

//first time extension function
fun String.containsSpecialCharacters(): Boolean {
    val regex = "[!@#\$%^&*(),.?\":{}/\\|'<>]".toRegex()
    return regex.containsMatchIn(this)
}